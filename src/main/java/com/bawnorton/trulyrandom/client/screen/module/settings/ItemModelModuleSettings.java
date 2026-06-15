package com.bawnorton.trulyrandom.client.screen.module.settings;

import com.bawnorton.trulyrandom.client.extend.CycleButtonExtender;
import com.bawnorton.trulyrandom.random.module.state.ItemModelModuleState;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ItemModelModuleSettings extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 15, 36);
    private final ItemModelModuleState moduleState;
    private final Screen parent;
    private boolean matchBlockModelRandomisation;

    public ItemModelModuleSettings(Component title, Screen parent, ItemModelModuleState moduleState) {
        super(title);
        this.parent = parent;
        this.moduleState = moduleState;
        this.matchBlockModelRandomisation = moduleState.isMatchingBlockModelRandomisation();
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
        layout.addToHeader(new MultiLineTextWidget(Component.translatable("selectWorld.trulyrandom.item_model_settings"), font), positioner -> positioner.paddingTop(15));
    }

    protected void addBody() {
        GridLayout columns = layout.addToContents(new GridLayout());
        columns.rowSpacing(2);
        GridLayout.RowHelper rowHelper = columns.createRowHelper(2);
        StringWidget matchBlockModelRandomisationTitle = new StringWidget(
                0,
                0,
                170,
                17,
                Component.translatable("selectWorld.trulyrandom.item_model_settings.match_block_model_randomisation"),
                font
        );
        CycleButton<Boolean> matchBlockModelRandomisationToggle = CycleButtonExtender.colouredOnOffButton(matchBlockModelRandomisation)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> matchBlockModelRandomisation = value);
        matchBlockModelRandomisationToggle.setTooltip(Tooltip.create(Component.translatable("selectWorld.trulyrandom.item_model_settings.match_block_model_randomisation.tooltip")));
        rowHelper.addChild(matchBlockModelRandomisationTitle);
        rowHelper.addChild(matchBlockModelRandomisationToggle);
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
        moduleState.setMatchBlockModelRandomisation(matchBlockModelRandomisation);
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
