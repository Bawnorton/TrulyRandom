package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2fStack;

public class SmithingGraphElement extends CraftingStationGraphElement {
    private static final Identifier SMITHING = TrulyRandom.id("loot_book/smithing");

    public SmithingGraphElement(RecipeHolder<?> recipe) {
        super(recipe, Items.SMITHING_TABLE);
    }

    @Override
    protected void extractRecipeTooltip(GuiGraphicsExtractor graphics, RecipeHolder<?> recipe, int mouseX, int mouseY) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SMITHING,
                mouseX + (BACKGROUND_WIDTH - 54) / 2,
                mouseY + (BACKGROUND_HEIGHT - 9) / 2,
                54,
                9
        );
        if(recipe.value() instanceof SmithingRecipe smithingRecipe) {
            Matrix3x2fStack matrices = graphics.pose();
            matrices.pushMatrix();
            matrices.scale(0.5f, 0.5f);
            int x = mouseX * 2;
            int y = mouseY * 2;
            smithingRecipe.templateIngredient().ifPresent(ingredient -> extractIngredient(graphics, ingredient, x + 20, y + 25, true));
            extractIngredient(graphics, smithingRecipe.baseIngredient(), x + 39, y + 25, true);
            smithingRecipe.additionIngredient().ifPresent(ingredient -> extractIngredient(graphics, ingredient, x + 57, y + 25, true));
            extractOutput(graphics, x + 111, y + 25);
            matrices.popMatrix();
        }
    }
}
