package com.bawnorton.trulyrandom.client.screen.module;

import com.bawnorton.trulyrandom.random.module.state.BlockModelModuleState;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class BlockModelModuleSettings extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 15, 36);
    private final BlockModelModuleState moduleState;
    private final Screen parent;
    private boolean isIgnoreModelOcclusion;
    private boolean isIgnoreStateProperties;

    public BlockModelModuleSettings(Component title, Screen parent, BlockModelModuleState moduleState) {
        super(title);
        this.parent = parent;
        this.moduleState = moduleState;
        this.isIgnoreModelOcclusion = moduleState.isIgnoreModelOcclusion();
        this.isIgnoreStateProperties = moduleState.isIgnoreStateProperties();
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
        layout.addToHeader(new MultiLineTextWidget(Component.translatable("selectWorld.trulyrandom.block_models_settings"), font), positioner -> positioner.paddingTop(15));
    }

    protected void addBody() {
        GridLayout columns = layout.addToContents(new GridLayout());
        columns.rowSpacing(2);
        GridLayout.RowHelper rowHelper = columns.createRowHelper(2);
        StringWidget ignoreModelOcclusionTitle = new StringWidget(
                0,
                0,
                130,
                17,
                Component.translatable("selectWorld.trulyrandom.block_models_settings.ignore_model_occlusion"),
                font
        );
        CycleButton<Boolean> ignoreModelOcclusionToggle = CycleButton.onOffBuilder(isIgnoreModelOcclusion)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> isIgnoreModelOcclusion = value);
        ignoreModelOcclusionToggle.setTooltip(Tooltip.create(Component.translatable("selectWorld.trulyrandom.block_models_settings.ignore_model_occlusion.tooltip")));
        rowHelper.addChild(ignoreModelOcclusionTitle);
        rowHelper.addChild(ignoreModelOcclusionToggle);

        StringWidget ignoreStatePropertiesTitle = new StringWidget(
                0,
                0,
                130,
                17,
                Component.translatable("selectWorld.trulyrandom.block_models_settings.ignore_state_properties"),
                font
        );
        CycleButton<Boolean> ignoreStatePropertiesToggle = CycleButton.onOffBuilder(isIgnoreStateProperties)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> isIgnoreStateProperties = value);
        ignoreStatePropertiesToggle.setTooltip(Tooltip.create(Component.translatable("selectWorld.trulyrandom.block_models_settings.ignore_state_properties.tooltip")));
        rowHelper.addChild(ignoreStatePropertiesTitle);
        rowHelper.addChild(ignoreStatePropertiesToggle);
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
        moduleState.setIgnoreModelOcclusion(isIgnoreModelOcclusion);
        moduleState.setIgnoreStateProprties(isIgnoreStateProperties);
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
