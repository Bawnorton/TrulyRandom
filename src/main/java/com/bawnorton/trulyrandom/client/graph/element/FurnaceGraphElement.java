package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeHolder;
import net.minecraft.util.Identifier;

public class FurnaceGraphElement extends CraftingStationGraphElement {
    private static final Identifier FURNACE = TrulyRandom.id("loot_book/furnace");

    public FurnaceGraphElement(RecipeHolder<?> recipe, Item smelter) {
        super(recipe, smelter);
    }

    @Override
    protected void renderRecipeTooltip(DrawContext context, RecipeHolder<?> recipe, int mouseX, int mouseY) {
        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                FURNACE,
                mouseX + (BACKGROUND_WIDTH - 41) / 2,
                mouseY + (BACKGROUND_HEIGHT - 27) / 2,
                41,
                27
        );
        if(recipe.value() instanceof AbstractCookingRecipe cookingRecipe) {
            context.getMatrices().pushMatrix();
            context.getMatrices().scale(0.5f, 0.5f);
            mouseX *= 2;
            mouseY *= 2;
            renderIngredient(context, cookingRecipe.ingredient(), mouseX + 35, mouseY + 7, true);
            renderIngredient(context, Ingredient.ofItem(Items.COAL), mouseX + 35, mouseY + 43, false);
            renderOutput(context, mouseX + 94, mouseY + 25);
            context.getMatrices().popMatrix();
        }
    }
}
