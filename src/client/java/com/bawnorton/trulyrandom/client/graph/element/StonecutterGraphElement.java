package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.util.Identifier;

public class StonecutterGraphElement extends CraftingStationGraphElement {
    private static final Identifier STONECUTTER = TrulyRandom.id("loot_book/stonecutter");

    public StonecutterGraphElement(RecipeEntry<?> recipe) {
        super(recipe, Items.STONECUTTER);
    }

    @Override
    protected void renderRecipeTooltip(DrawContext context, RecipeEntry<?> recipe, int mouseX, int mouseY) {
        context.drawGuiTexture(
                RenderLayer::getGuiTextured,
                STONECUTTER,
                mouseX + (BACKGROUND_WIDTH - 41) / 2,
                mouseY + (BACKGROUND_HEIGHT - 13) / 2,
                41,
                13
        );
        if(recipe.value() instanceof StonecuttingRecipe stonecuttingRecipe) {
            context.getMatrices().push();
            context.getMatrices().scale(0.5f, 0.5f, 1);
            int x = mouseX * 2;
            int y = mouseY * 2;
            renderIngredient(context, stonecuttingRecipe.ingredient(), x + 38, y + 25, true);
            renderOutput(context, x + 94, y + 25);
            context.getMatrices().pop();
        }
    }
}
