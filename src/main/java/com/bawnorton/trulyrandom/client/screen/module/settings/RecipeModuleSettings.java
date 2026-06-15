package com.bawnorton.trulyrandom.client.screen.module.settings;

import com.bawnorton.trulyrandom.client.extend.CycleButtonExtender;
import com.bawnorton.trulyrandom.random.module.state.RecipeModuleState;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
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

public class RecipeModuleSettings extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 15, 36);
    private final RecipeModuleState moduleState;
    private final Screen parent;
    private final Map<RecipeType<?>, Boolean> enabledRecipeTypes;

    public RecipeModuleSettings(Component title, Screen parent, RecipeModuleState moduleState) {
        super(title);
        this.parent = parent;
        this.moduleState = moduleState;
        this.enabledRecipeTypes = new HashMap<>(moduleState.getEnabledRecipeTypes());
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
        layout.addToHeader(new MultiLineTextWidget(Component.translatable("selectWorld.trulyrandom.recipe_settings"), font), positioner -> positioner.paddingTop(15));
    }

    protected void addBody() {
        GridLayout columns = layout.addToContents(new GridLayout());
        columns.rowSpacing(2);
        GridLayout.RowHelper rowHelper = columns.createRowHelper(2);
        enabledRecipeTypes.entrySet().stream().sorted(Comparator.comparing(entry -> entry.getKey().toString())).forEach(entry -> {
            RecipeType<?> recipeType = entry.getKey();
            Boolean enabled = entry.getValue();
            StringWidget specialReciesTitle = new StringWidget(
                    0,
                    0,
                    130,
                    17,
                    Component.translatable("selectWorld.trulyrandom.recipe_settings.%s".formatted(BuiltInRegistries.RECIPE_TYPE.getKey(recipeType))),
                    font
            );
            CycleButton<Boolean> specialRecipesToggle = CycleButtonExtender.colouredOnOffButton(enabled)
                    .displayOnlyValue()
                    .create(0, 0, 44, 17, Component.empty(), (_, value) -> setEnabledRecipeType(recipeType, value));
            rowHelper.addChild(specialReciesTitle);
            rowHelper.addChild(specialRecipesToggle);
        });
    }

    protected void addFooter() {
        GridLayout.RowHelper rowHelper = layout.addToFooter(new GridLayout().columnSpacing(10)).createRowHelper(2);
        rowHelper.addChild(Button.builder(CommonComponents.GUI_DONE, _ -> applyAndClose()).build());
        rowHelper.addChild(Button.builder(CommonComponents.GUI_CANCEL, _ -> forgetAndClose()).build());
    }

    private void setEnabledRecipeType(RecipeType<?> recipeType, Boolean value) {
        enabledRecipeTypes.put(recipeType, value);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    public void applyAndClose() {
        enabledRecipeTypes.forEach((moduleState::setRecipeTypeEnabled));
        onClose();
    }

    public void forgetAndClose() {
        enabledRecipeTypes.clear();
        onClose();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
    }
}
