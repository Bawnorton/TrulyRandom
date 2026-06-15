package com.bawnorton.trulyrandom.client.screen.module.settings;

import com.bawnorton.trulyrandom.client.extend.CycleButtonExtender;
import com.bawnorton.trulyrandom.random.module.state.StructureModuleState;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class StructureModuleSettings extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 15, 36);
    private final StructureModuleState moduleState;
    private final Screen parent;
    private boolean legacyRandomiser;
    private boolean nerfElytra;

    public StructureModuleSettings(Component title, Screen parent, StructureModuleState moduleState) {
        super(title);
        this.parent = parent;
        this.moduleState = moduleState;
        this.legacyRandomiser = moduleState.useLegacyRandomiser();
        this.nerfElytra = moduleState.isNerfElytra();
    }

    @Override
    protected void init() {
        addHeader();
        addBody();
        addFooter();
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    protected void addHeader() {
        layout.addToHeader(new MultiLineTextWidget(Component.translatable("selectWorld.trulyrandom.structure_settings"), font), positioner -> positioner.paddingTop(15));
    }

    protected void addBody() {
        GridLayout columns = layout.addToContents(new GridLayout());
        columns.rowSpacing(2);
        GridLayout.RowHelper rowHelper = columns.createRowHelper(2);
        StringWidget useLegacyRandomiserTitle = new StringWidget(
                0,
                0,
                160,
                17,
                Component.translatable("selectWorld.trulyrandom.structure_settings.use_legacy_randomiser"),
                font
        );
        CycleButton<Boolean> useLegacyRandomiserToggle = CycleButtonExtender.colouredOnOffButton(legacyRandomiser)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> legacyRandomiser = value);
        useLegacyRandomiserToggle.setTooltip(Tooltip.create(Component.translatable("selectWorld.trulyrandom.structure_settings.use_legacy_randomiser.tooltip")));
        rowHelper.addChild(useLegacyRandomiserTitle);
        rowHelper.addChild(useLegacyRandomiserToggle);

        StringWidget nerfElytraTitle = new StringWidget(
                0,
                0,
                160,
                17,
                Component.translatable("selectWorld.trulyrandom.structure_settings.nerf_elytra"),
                font
        );
        CycleButton<Boolean> nerfElytraToggle = CycleButtonExtender.colouredOnOffButton(nerfElytra)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> nerfElytra = value);
        nerfElytraToggle.setTooltip(Tooltip.create(Component.translatable("selectWorld.trulyrandom.structure_settings.nerf_elytra.tooltip")));
        rowHelper.addChild(nerfElytraTitle);
        rowHelper.addChild(nerfElytraToggle);
    }

    protected void addFooter() {
        GridLayout.RowHelper rowHelper = layout.addToFooter(new GridLayout().columnSpacing(10)).createRowHelper(2);
        rowHelper.addChild(Button.builder(CommonComponents.GUI_DONE, _ -> applyAndClose()).build());
        rowHelper.addChild(Button.builder(CommonComponents.GUI_CANCEL, _ -> forgetAndClose()).build());
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    public void applyAndClose() {
        moduleState.setUseLegacyRandomiser(legacyRandomiser);
        moduleState.setNerfElytra(nerfElytra);
        onClose();
    }

    public void forgetAndClose() {
        onClose();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
    }
}
