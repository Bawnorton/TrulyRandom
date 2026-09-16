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
                mouseX + (BACKGROUND_WIDTH - 108) / 2,
                mouseY + (BACKGROUND_HEIGHT - 18) / 2,
                108,
                18
        );
        if(recipe.value() instanceof SmithingRecipe smithingRecipe) {
            smithingRecipe.templateIngredient().ifPresent(ingredient -> extractIngredient(graphics, ingredient, mouseX + 21, mouseY + 25, true));
            extractIngredient(graphics, smithingRecipe.baseIngredient(), mouseX + 40, mouseY + 25, true);
            smithingRecipe.additionIngredient().ifPresent(ingredient -> extractIngredient(graphics, ingredient, mouseX + 58, mouseY + 25, true));
            extractOutput(graphics, mouseX + 112, mouseY + 25);
        }
    }
}
