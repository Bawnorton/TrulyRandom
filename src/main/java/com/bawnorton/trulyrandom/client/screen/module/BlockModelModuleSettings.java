package com.bawnorton.trulyrandom.client.screen.module;

import com.bawnorton.trulyrandom.random.module.state.BlockModelModuleState;
import com.bawnorton.trulyrandom.random.module.state.RecipeModuleState;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BlockModelModuleSettings extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 15, 36);
    private final BlockModelModuleState moduleState;
    private final Screen parent;
    private boolean isIgnoreModelOcclusion;

    public BlockModelModuleSettings(Component title, Screen parent, BlockModelModuleState moduleState) {
        super(title);
        this.parent = parent;
        this.moduleState = moduleState;
        this.isIgnoreModelOcclusion = moduleState.isIgnoreModelOcclusion();
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
        StringWidget specialReciesTitle = new StringWidget(
                0,
                0,
                130,
                17,
                Component.translatable("selectWorld.trulyrandom.block_models_settings.ignore_model_occlusion"),
                font
        );
        CycleButton<Boolean> specialRecipesToggle = CycleButton.onOffBuilder(isIgnoreModelOcclusion)
                .displayOnlyValue()
                .create(0, 0, 44, 17, Component.empty(), (_, value) -> isIgnoreModelOcclusion = value);
        specialRecipesToggle.setTooltip(Tooltip.create(Component.literal("Ignoring model occlussion can cause solid blocks to be replaced with non-solid block models allowing you to see through the world, which can cause significant FPS lag as no culling will be applied to these blocks.")));
        rowHelper.addChild(specialReciesTitle);
        rowHelper.addChild(specialRecipesToggle);
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
