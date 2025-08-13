package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import java.util.List;

public class CraftingTableGraphElement extends CraftingStationGraphElement {
    private static final Identifier CRAFTING = TrulyRandom.id("loot_book/crafting");

    public CraftingTableGraphElement(RecipeEntry<?> recipe) {
        super(recipe, Items.CRAFTING_TABLE);
    }

    @Override
    protected void renderRecipeTooltip(DrawContext context, RecipeEntry<?> recipe, int mouseX, int mouseY) {
        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                CRAFTING,
                mouseX + (BACKGROUND_WIDTH - 58) / 2,
                mouseY + (BACKGROUND_HEIGHT - 27) / 2,
                58,
                27
        );
        IngredientPlacement placement = recipe.value().getIngredientPlacement();
        List<Ingredient> ingredients = placement.getIngredients();
        IntList slots = placement.getPlacementSlots();

        context.getMatrices().pushMatrix();
        context.getMatrices().scale(0.5f, 0.5f);
        mouseX *= 2;
        mouseY *= 2;
        int skipCount = 0;
        int width = 3;
        if(recipe.value() instanceof ShapedRecipe shapedRecipe) {
            width = shapedRecipe.getWidth();
        }
        for(int i = 0; i < slots.size(); i++) {
            int slot = slots.getInt(i);
            if(slot == -1) {
                skipCount++;
                continue;
            }

            int x = mouseX + 17 + ((slot + skipCount) % width) * 18;
            int y = mouseY + 7 + ((slot + skipCount) / width) * 18;
            renderIngredient(context, ingredients.get(slot), x, y, true);
        }
        renderOutput(context, mouseX + 111, mouseY + 25);
        context.getMatrices().popMatrix();
    }
}
