package com.bawnorton.trulyrandom.client.screen.module.settings;

import com.bawnorton.trulyrandom.client.extend.CycleButtonExtender;
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
    private boolean isForcingStatesToUseSameModel;
    private boolean isIgnoreModelOcclusion;
    private boolean isIgnoreStateProperties;

    public BlockModelModuleSettings(Component title, Screen parent, BlockModelModuleState moduleState) {
        super(title);
        this.parent = parent;
        this.moduleState = moduleState;
        this.isForcingStatesToUseSameModel = moduleState.isForcingStatesToUseSameModel();
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
        StringWidget forceStatesToUseSameModelTitle = new StringWidget(
                0,
                0,
                150,
                17,
                Component.translatable("selectWorld.trulyrandom.block_models_settings.force_states_to_use_same_model"),
                font
        );
        CycleButton<Boolean> forceStatesToUseSameModelToggle = CycleButtonExtender.colouredOnOffButton(isForcingStatesToUseSameModel)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> isForcingStatesToUseSameModel = value);
        forceStatesToUseSameModelToggle.setTooltip(Tooltip.create(Component.translatable("selectWorld.trulyrandom.block_models_settings.force_states_to_use_same_model.tooltip")));
        rowHelper.addChild(forceStatesToUseSameModelTitle);
        rowHelper.addChild(forceStatesToUseSameModelToggle);

        StringWidget ignoreModelOcclusionTitle = new StringWidget(
                0,
                0,
                150,
                17,
                Component.translatable("selectWorld.trulyrandom.block_models_settings.ignore_model_occlusion"),
                font
        );
        CycleButton<Boolean> ignoreModelOcclusionToggle = CycleButtonExtender.colouredOnOffButton(isIgnoreModelOcclusion)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> isIgnoreModelOcclusion = value);
        ignoreModelOcclusionToggle.setTooltip(Tooltip.create(Component.translatable("selectWorld.trulyrandom.block_models_settings.ignore_model_occlusion.tooltip")));
        rowHelper.addChild(ignoreModelOcclusionTitle);
        rowHelper.addChild(ignoreModelOcclusionToggle);

        StringWidget ignoreStatePropertiesTitle = new StringWidget(
                0,
                0,
                150,
                17,
                Component.translatable("selectWorld.trulyrandom.block_models_settings.ignore_state_properties"),
                font
        );
        CycleButton<Boolean> ignoreStatePropertiesToggle = CycleButtonExtender.colouredOnOffButton(isIgnoreStateProperties)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> {
                    isIgnoreStateProperties = value;
                    if(value) {
                        forceStatesToUseSameModelToggle.setValue(false);
                    }
                });
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
        moduleState.setForceStatesToUseSameModel(isForcingStatesToUseSameModel);
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
