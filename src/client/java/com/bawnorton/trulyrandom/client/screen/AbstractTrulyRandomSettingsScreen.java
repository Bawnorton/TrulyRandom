package com.bawnorton.trulyrandom.client.screen;

import com.bawnorton.trulyrandom.client.screen.widget.ColumnedOptionGrid;
import com.bawnorton.trulyrandom.client.screen.widget.DirectionalLayoutWidget;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.function.Consumer;

public abstract class AbstractTrulyRandomSettingsScreen extends Screen {
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 0, 36);
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
        initTabNavigation();
    }

    protected void addHeader() {
        // no-op
    }

    protected void addModules() {
        DirectionalLayoutWidget content = layout.addBody(DirectionalLayoutWidget.vertical());
        content.add(
                new MultilineTextWidget(getContentText(), textRenderer).setMaxWidth(340),
                positioner -> positioner.marginBottom(15)
        );
        ColumnedOptionGrid.Builder builder = ColumnedOptionGrid.builder(2, 310);
        getModules().forEachVisible(module -> {
            builder.add(Text.translatable("selectWorld.trulyrandom." + module.name().toLowerCase()))
                   .value(() -> getModules().getMemento(module), enabled -> getModules().setMemento(module, enabled))
                   .seed(() -> getModules().getSeedMemento(module), seed -> getModules().setSeedMemento(module, seed))
                   .toggleable(module::isImplemented)
                   .tooltip(() -> {
                       if (module.isImplemented()) {
                           return Text.translatable("selectWorld.trulyrandom." + module.name().toLowerCase() + ".tooltip");
                       } else {
                           return Text.translatable("selectWorld.trulyrandom.not_implemented")
                                      .formatted(Formatting.DARK_GRAY)
                                      .formatted(Formatting.ITALIC);
                       }
                   })
                    .seedTooltip(() -> {
                        if (module.isImplemented() && module.isMutable()) {
                            return Text.translatable("selectWorld.trulyrandom.seed_tooltip");
                        } else if (!module.isMutable()) {
                            return Text.translatable("selectWorld.trulyrandom.seed_tooltip_immutable");
                        } else {
                            return Text.translatable("selectWorld.trulyrandom.not_implemented")
                                       .formatted(Formatting.DARK_GRAY)
                                       .formatted(Formatting.ITALIC);
                        }
                    })
                   .toggleable(() -> {
                       if (client.world == null) return true;
                       return module.isMutable();
                   });
        });
        builder.build(content::add);
    }

    protected void addFooter() {
        GridWidget.Adder adder = layout.addFooter(new GridWidget().setColumnSpacing(10)).createAdder(2);
        adder.add(ButtonWidget.builder(ScreenTexts.DONE, button -> applyAndClose()).build());
        adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> forgetAndClose()).build());
        layout.forEachChild(this::addDrawableChild);
    }

    protected Text getContentText() {
        return Text.translatable("selectWorld.trulyrandom.info");
    }

    protected abstract Modules getModules();

    @Override
    public void close() {
        assert client != null;
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
    protected void initTabNavigation() {
        layout.refreshPositions();
    }
}