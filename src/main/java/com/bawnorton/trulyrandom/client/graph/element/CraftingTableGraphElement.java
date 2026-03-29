package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.joml.Matrix3x2fStack;

import java.util.List;

public class CraftingTableGraphElement extends CraftingStationGraphElement {
    private static final Identifier CRAFTING = TrulyRandom.id("loot_book/crafting");

    public CraftingTableGraphElement(RecipeHolder<?> recipe) {
        super(recipe, Items.CRAFTING_TABLE);
    }

    @Override
    protected void extractRecipeTooltip(GuiGraphicsExtractor graphics, RecipeHolder<?> recipe, int mouseX, int mouseY) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                CRAFTING,
                mouseX + (BACKGROUND_WIDTH - 58) / 2,
                mouseY + (BACKGROUND_HEIGHT - 27) / 2,
                58,
                27
        );
        PlacementInfo placement = recipe.value().placementInfo();
        List<Ingredient> ingredients = placement.ingredients();
        IntList slots = placement.slotsToIngredientIndex();

        Matrix3x2fStack matrices = graphics.pose();
        matrices.pushMatrix();
        matrices.scale(0.5f, 0.5f);
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
            extractIngredient(graphics, ingredients.get(slot), x, y, true);
        }
        extractOutput(graphics, mouseX + 111, mouseY + 25);
        matrices.popMatrix();
    }
}
