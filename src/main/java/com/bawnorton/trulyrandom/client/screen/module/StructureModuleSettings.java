package com.bawnorton.trulyrandom.client.screen.module;

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
    private boolean replaceStructuresInstead;

    public StructureModuleSettings(Component title, Screen parent, StructureModuleState moduleState) {
        super(title);
        this.parent = parent;
        this.moduleState = moduleState;
        this.replaceStructuresInstead = moduleState.replaceStructuresInstead();
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
        StringWidget replaceStructureInsteadTitle = new StringWidget(
                0,
                0,
                160,
                17,
                Component.translatable("selectWorld.trulyrandom.structure_settings.replace_structures_instead"),
                font
        );
        CycleButton<Boolean> replaceStructureInsteadToggle = CycleButton.onOffBuilder(replaceStructuresInstead)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> replaceStructuresInstead = value);
        replaceStructureInsteadToggle.setTooltip(Tooltip.create(Component.translatable("selectWorld.trulyrandom.structure_settings.replace_structures_instead.tooltip")));
        rowHelper.addChild(replaceStructureInsteadTitle);
        rowHelper.addChild(replaceStructureInsteadToggle);
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
        moduleState.setReplaceStructuresInstead(replaceStructuresInstead);
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
