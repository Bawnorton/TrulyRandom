package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import org.joml.Matrix3x2fStack;

public class StonecutterGraphElement extends CraftingStationGraphElement {
    private static final Identifier STONECUTTER = TrulyRandom.id("loot_book/stonecutter");

    public StonecutterGraphElement(RecipeHolder<?> recipe) {
        super(recipe, Items.STONECUTTER);
    }

    @Override
    protected void extractRecipeTooltip(GuiGraphicsExtractor graphics, RecipeHolder<?> recipe, int mouseX, int mouseY) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                STONECUTTER,
                mouseX + (BACKGROUND_WIDTH - 41) / 2,
                mouseY + (BACKGROUND_HEIGHT - 13) / 2,
                41,
                13
        );
        if(recipe.value() instanceof StonecutterRecipe stonecutterRecipe) {
            Matrix3x2fStack matrices = graphics.pose();
            matrices.pushMatrix();
            matrices.scale(0.5f, 0.5f);
            int x = mouseX * 2;
            int y = mouseY * 2;
            extractIngredient(graphics, stonecutterRecipe.input(), x + 38, y + 25, true);
            extractOutput(graphics, x + 94, y + 25);
            matrices.popMatrix();
        }
    }
}
