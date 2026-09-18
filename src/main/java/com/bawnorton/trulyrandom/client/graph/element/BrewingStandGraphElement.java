//? if >=26.3 {
package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.util.Cycler;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Comparator;
import java.util.List;

public class BrewingStandGraphElement extends CraftingStationGraphElement {
    private static final Identifier BREWING_STAND = TrulyRandom.id("loot_book/brewing_stand");

    public BrewingStandGraphElement(RecipeHolder<?> recipe) {
        super(recipe, Items.BREWING_STAND);
    }

    @Override
    protected void extractRecipeTooltip(GuiGraphicsExtractor graphics, RecipeHolder<?> recipe, int mouseX, int mouseY) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                BREWING_STAND,
                mouseX + (BACKGROUND_WIDTH - 103) / 2,
                mouseY + (BACKGROUND_HEIGHT - 59) / 2,
                103,
                59
        );
        if(recipe.value() instanceof BrewingRecipe brewingRecipe) {
            extractIngredient(graphics, Ingredient.of(Items.BLAZE_POWDER), mouseX + 24, mouseY + 3, false);
            extractPotionIngredient(graphics, brewingRecipe.getReagent(), mouseX + 86, mouseY + 3);
            extractPotionIngredient(graphics, brewingRecipe.getInput(), mouseX + 62, mouseY + 38);
            extractOutput(graphics, mouseX + 109, mouseY + 38);
        }
    }

    private void extractPotionIngredient(GuiGraphicsExtractor graphics, PotionIngredient ingredient, int x, int y) {
        List<Item> items = ingredient.ingredient()
                .items()
                .sorted(Comparator.comparing(entry -> entry.unwrapKey().map(ResourceKey::identifier).orElse(TrulyRandom.id("missing"))))
                .map(Holder::value)
                .toList();

        Cycler<Item> cycler = cyclers.computeIfAbsent(items.hashCode(), _ -> new Cycler<>(items));
        Item item = cycler.current();
        if (item == null) return;

        ItemStack stack = item.getDefaultInstance();
        ingredient.potions().ifPresent(predicate -> predicate.potions().ifPresent(set -> set.forEach(potion -> {
            PotionContents contents = new PotionContents(potion);
            stack.set(predicate.componentType(), contents);
        })));

        graphics.fakeItem(stack, x, y);
    }
}
//?}