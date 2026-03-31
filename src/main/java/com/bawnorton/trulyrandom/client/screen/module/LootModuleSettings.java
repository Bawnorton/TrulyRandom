package com.bawnorton.trulyrandom.client.screen.module;

import com.bawnorton.trulyrandom.random.module.state.LootModuleState;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class LootModuleSettings extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 15, 36);
    private final LootModuleState moduleState;
    private final Screen parent;
    private boolean useOtherLootTables;

    public LootModuleSettings(Component title, Screen parent, LootModuleState moduleState) {
        super(title);
        this.parent = parent;
        this.moduleState = moduleState;
        this.useOtherLootTables = moduleState.useOtherLootTables();
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
        layout.addToHeader(new MultiLineTextWidget(Component.translatable("selectWorld.trulyrandom.loot_settings"), font), positioner -> positioner.paddingTop(15));
    }

    protected void addBody() {
        GridLayout columns = layout.addToContents(new GridLayout());
        columns.rowSpacing(2);
        GridLayout.RowHelper rowHelper = columns.createRowHelper(2);
        StringWidget useOtherLootTablesTitle = new StringWidget(
                0,
                0,
                130,
                17,
                Component.translatable("selectWorld.trulyrandom.loot_settings.use_other_loot_tables"),
                font
        );
        CycleButton<Boolean> useOtherLootTablesToggle = CycleButton.onOffBuilder(useOtherLootTables)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> useOtherLootTables = value);
        useOtherLootTablesToggle.setTooltip(Tooltip.create(Component.literal("Whether the randomisation should swap loot tables or produce random items with a random quantity. Swapping loot tables is the default and is recommended as it allows for more consistent randomisation and better compatibility with other mods. Disabling also disables the tracker as it would be redundant.")));
        rowHelper.addChild(useOtherLootTablesTitle);
        rowHelper.addChild(useOtherLootTablesToggle);
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
        moduleState.setUseOtherLootTables(useOtherLootTables);
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
