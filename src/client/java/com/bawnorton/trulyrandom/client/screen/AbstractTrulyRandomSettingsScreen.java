package com.bawnorton.trulyrandom.client.screen;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.network.ClientNetworking;
import com.bawnorton.trulyrandom.client.screen.module.ModuleWidgetSettings;
import com.bawnorton.trulyrandom.client.screen.module.adapter.DefaultModuleWidgetAdapter;
import com.bawnorton.trulyrandom.client.screen.module.adapter.RecipeModuleWidgetAdapter;
import com.bawnorton.trulyrandom.client.screen.module.adapter.StructureModuleWidgetAdapter;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.registry.TrulyRandomCriteria;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class AbstractTrulyRandomSettingsScreen extends Screen {
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 15, 36);
    private final Screen parent;
    private final Consumer<Modules> applier;

    protected AbstractTrulyRandomSettingsScreen(Screen parent, Consumer<Modules> applier) {
        super(Text.translatable("selectWorld.trulyrandom"));
        this.parent = parent;
        this.applier = applier;
    }

    @Override
    protected void init() {
        addHeader();
        addModules();
        addFooter();
        layout.forEachChild(this::addDrawableChild);
        refreshWidgetPositions();
    }

    protected void addHeader() {
        layout.addHeader(new MultilineTextWidget(getContentText(), textRenderer), positioner -> positioner.marginTop(15));
    }

    protected void addModules() {
        GridWidget columns = layout.addBody(new GridWidget());
        columns.setRowSpacing(10);
        GridWidget.Adder columnsAdder = columns.createAdder(1);
        List<Module> modules = getModules().asList();
        modules.removeIf(module -> !getModules().isVisible(module));
        modules.sort(Comparator.comparingInt(Module::ordinal));
        ModuleWidgetSettings moduleSettings = new ModuleWidgetSettings(client, getModules());
        moduleSettings.registerAdapters(Set.of(Module.STRUCTURES, Module.FEATURES), new StructureModuleWidgetAdapter());
        moduleSettings.registerAdapters(Set.of(Module.RECIPES), new RecipeModuleWidgetAdapter());
        moduleSettings.setDefaultAdapter(new DefaultModuleWidgetAdapter());
        GridWidget.Adder rowAdder = null;
        for (int i = 0; i < modules.size(); i++) {
            if(i % 2 == 0) {
                GridWidget row = columnsAdder.add(new GridWidget());
                row.setColumnSpacing(10);
                rowAdder = row.createAdder(2);
            }
            Module module = modules.get(i);
            rowAdder.add(moduleSettings.getWidget(module));
        }
    }

    protected void addFooter() {
        GridWidget.Adder adder = layout.addFooter(new GridWidget().setColumnSpacing(10)).createAdder(2);
        adder.add(ButtonWidget.builder(ScreenTexts.DONE, button -> applyAndClose()).build());
        adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> forgetAndClose()).build());
    }

    protected Text getContentText() {
        return Text.translatable("selectWorld.trulyrandom.info");
    }

    protected abstract Modules getModules();

    @Override
    public void close() {
        client.setScreen(parent);
    }

    private void applyAndClose() {
        getModules().confirm();
        applier.accept(getModules());
        close();
    }

    private void forgetAndClose() {
        getModules().cancel();
        close();
    }

    @Override
    protected void refreshWidgetPositions() {
        layout.refreshPositions();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(
                client.textRenderer,
                Text.literal(TrulyRandom.VERSION.getFriendlyString()),
                5,
                5,
                Colors.LIGHT_GRAY,
                false
        );
    }
}