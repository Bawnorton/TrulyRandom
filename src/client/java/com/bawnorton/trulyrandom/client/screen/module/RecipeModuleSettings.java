package com.bawnorton.trulyrandom.client.screen.module;

import com.bawnorton.trulyrandom.random.module.RecipeModuleState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;

public class RecipeModuleSettings extends Screen {
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this, 15, 36);
    private final RecipeModuleState moduleState;
    private final Screen parent;
    private final Map<RecipeType<?>, Boolean> enabledRecipeTypes;

    public RecipeModuleSettings(Text title, Screen parent, RecipeModuleState moduleState) {
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
        layout.forEachChild(this::addDrawableChild);
        initTabNavigation();
    }

    protected void addHeader() {
        layout.addHeader(new MultilineTextWidget(Text.translatable("selectWorld.trulyrandom.recipe_settings"), textRenderer), positioner -> positioner.marginTop(15));
    }

    protected void addBody() {
        GridWidget columns = layout.addBody(new GridWidget());
        columns.setRowSpacing(2);
        GridWidget.Adder adder = columns.createAdder(2);
        enabledRecipeTypes.forEach((recipeType, enabled) -> {
            TextWidget specialReciesTitle = new TextWidget(
                    0,
                    0,
                    130,
                    17,
                    Text.translatable("selectWorld.trulyrandom.recipe_settings.%s".formatted(Registries.RECIPE_TYPE.getId(recipeType))),
                    textRenderer
            ).alignLeft();
            CyclingButtonWidget<Boolean> specialRecipesToggle = CyclingButtonWidget.onOffBuilder()
                    .initially(enabled)
                    .omitKeyText()
                    .build(0, 0, 44, 17, Text.empty(), (button, value) -> setEnabledRecipeType(recipeType, value));
            adder.add(specialReciesTitle);
            adder.add(specialRecipesToggle);
        });
    }

    protected void addFooter() {
        GridWidget.Adder adder = layout.addFooter(new GridWidget().setColumnSpacing(10)).createAdder(2);
        adder.add(ButtonWidget.builder(ScreenTexts.DONE, button -> applyAndClose()).build());
        adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> forgetAndClose()).build());
    }

    private void setEnabledRecipeType(RecipeType<?> recipeType, Boolean value) {
        enabledRecipeTypes.put(recipeType, value);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    public void applyAndClose() {
        enabledRecipeTypes.forEach((moduleState::setRecipeTypeEnabled));
        close();
    }

    public void forgetAndClose() {
        enabledRecipeTypes.clear();
        close();
    }

    @Override
    protected void initTabNavigation() {
        layout.refreshPositions();
    }
}
