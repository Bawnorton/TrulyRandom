package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;

public class SmithingGraphElement extends CraftingStationGraphElement {
    private static final Identifier SMITHING = TrulyRandom.id("loot_book/smithing");

    public SmithingGraphElement(RecipeEntry<?> recipe) {
        super(recipe, Items.SMITHING_TABLE);
    }

    @Override
    protected void renderRecipeTooltip(DrawContext context, RecipeEntry<?> recipe, int mouseX, int mouseY) {
        context.drawGuiTexture(
                RenderLayer::getGuiTextured,
                SMITHING,
                mouseX + (BACKGROUND_WIDTH - 54) / 2,
                mouseY + (BACKGROUND_HEIGHT - 9) / 2,
                54,
                9
        );
        if(recipe.value() instanceof SmithingRecipe smithingRecipe) {
            context.getMatrices().push();
            context.getMatrices().scale(0.5f, 0.5f, 1);
            int x = mouseX * 2;
            int y = mouseY * 2;
            smithingRecipe.template().ifPresent(ingredient -> renderIngredient(context, ingredient, x + 20, y + 25, true));
            smithingRecipe.base().ifPresent(ingredient -> renderIngredient(context, ingredient, x + 39, y + 25, true));
            smithingRecipe.addition().ifPresent(ingredient -> renderIngredient(context, ingredient, x + 57, y + 25, true));
            renderOutput(context, x + 111, y + 25);
            context.getMatrices().pop();
        }
    }
}
