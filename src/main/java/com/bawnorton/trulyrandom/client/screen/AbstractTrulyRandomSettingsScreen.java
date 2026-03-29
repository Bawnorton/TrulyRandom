package com.bawnorton.trulyrandom.client.screen;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.screen.module.ModuleWidgetSettings;
import com.bawnorton.trulyrandom.client.screen.module.adapter.DefaultModuleWidgetAdapter;
import com.bawnorton.trulyrandom.client.screen.module.adapter.RecipeModuleWidgetAdapter;
import com.bawnorton.trulyrandom.client.screen.module.adapter.StructureModuleWidgetAdapter;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public abstract class AbstractTrulyRandomSettingsScreen extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 15, 36);
    private final Screen parent;
    private final Consumer<Modules> applier;

    protected AbstractTrulyRandomSettingsScreen(Screen parent, Consumer<Modules> applier) {
        super(Component.translatable("selectWorld.trulyrandom"));
        this.parent = parent;
        this.applier = applier;
    }

    @Override
    protected void init() {
        addHeader();
        addModules();
        addFooter();
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    protected void addHeader() {
        layout.addToHeader(new MultiLineTextWidget(getContentText(), font), positioner -> positioner.paddingTop(15));
    }

    protected void addModules() {
        GridLayout columns = layout.addToContents(new GridLayout());
        columns.rowSpacing(10);
        GridLayout.RowHelper rowHelper = columns.createRowHelper(1);
        List<Module> modules = getModules().asList();
        modules.removeIf(module -> !getModules().isVisible(module));
        modules.sort(Comparator.comparingInt(Module::ordinal));
        ModuleWidgetSettings moduleSettings = new ModuleWidgetSettings(minecraft, getModules());
        moduleSettings.registerAdapters(Set.of(Module.STRUCTURES, Module.FEATURES), new StructureModuleWidgetAdapter());
        moduleSettings.registerAdapters(Set.of(Module.RECIPES), new RecipeModuleWidgetAdapter());
        moduleSettings.setDefaultAdapter(new DefaultModuleWidgetAdapter());
        GridLayout.RowHelper subRowHelper = null;
        for (int i = 0; i < modules.size(); i++) {
            if(i % 2 == 0) {
                GridLayout row = rowHelper.addChild(new GridLayout());
                row.columnSpacing(10);
                subRowHelper = row.createRowHelper(2);
            }
            Module module = modules.get(i);
            subRowHelper.addChild(moduleSettings.getWidget(module));
        }
    }

    protected void addFooter() {
        GridLayout.RowHelper rowHelper = layout.addToFooter(new GridLayout().columnSpacing(10)).createRowHelper(2);
        rowHelper.addChild(Button.builder(CommonComponents.GUI_DONE, _ -> applyAndClose()).build());
        rowHelper.addChild(Button.builder(CommonComponents.GUI_CANCEL, _ -> forgetAndClose()).build());
    }

    protected Component getContentText() {
        return Component.translatable("selectWorld.trulyrandom.info");
    }

    protected abstract Modules getModules();

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private void applyAndClose() {
        getModules().confirm();
        applier.accept(getModules());
        onClose();
    }

    private void forgetAndClose() {
        getModules().cancel();
        onClose();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.text(
                minecraft.font,
                Component.literal(TrulyRandom.VERSION.getFriendlyString()),
                5,
                5,
                CommonColors.LIGHT_GRAY,
                false
        );
    }
}