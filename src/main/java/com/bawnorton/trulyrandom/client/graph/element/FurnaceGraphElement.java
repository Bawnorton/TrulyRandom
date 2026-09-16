package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.joml.Matrix3x2fStack;

public class FurnaceGraphElement extends CraftingStationGraphElement {
    private static final Identifier FURNACE = TrulyRandom.id("loot_book/furnace");

    public FurnaceGraphElement(RecipeHolder<?> recipe, Item smelter) {
        super(recipe, smelter);
    }

    @Override
    protected void extractRecipeTooltip(GuiGraphicsExtractor graphics, RecipeHolder<?> recipe, int mouseX, int mouseY) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                FURNACE,
                mouseX + (BACKGROUND_WIDTH - 82) / 2,
                mouseY + (BACKGROUND_HEIGHT - 54) / 2,
                82,
                54
        );
        if(recipe.value() instanceof AbstractCookingRecipe cookingRecipe) {
            extractIngredient(graphics, cookingRecipe.input(), mouseX + 34, mouseY + 6, true);
            extractIngredient(graphics, Ingredient.of(Items.COAL), mouseX + 34, mouseY + 43, false);
            extractOutput(graphics, mouseX + 94, mouseY + 25);
        }
    }
}
