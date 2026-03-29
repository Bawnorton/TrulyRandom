package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.util.Cycler;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public abstract class CraftingStationGraphElement extends GraphElement {
    protected static final Identifier RECIPE_BACKGROUND = TrulyRandom.id("loot_book/background");
    protected static final int BACKGROUND_WIDTH = 75;
    protected static final int BACKGROUND_HEIGHT = 33;

    private final RecipeHolder<?> recipe;
    private final Item station;

    protected CraftingStationGraphElement(RecipeHolder<?> recipe, Item station) {
        this.station = station;
        this.recipe = recipe;
    }

    @Override
    protected void onToldToHover() {
        super.onToldToHover();
        getFrom().forEach(GraphElement::onToldToHover);
    }

    @Override
    protected void onToldToClearHover() {
        super.onToldToClearHover();
        getFrom().forEach(GraphElement::onToldToClearHover);
    }

    @Override
    protected Component getTooltip() {
        return Component.translatable("container.crafting");
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        graphics.fakeItem(station.getDefaultInstance(), x - 8, y - 8);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        Identifier texture = AdvancementWidgetType.UNOBTAINED.frameSprite(AdvancementType.GOAL);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height, isHovered() ? 0xFF6666FF : 0xFFAAAAFF);
    }
    
    protected abstract void extractRecipeTooltip(GuiGraphicsExtractor graphics, RecipeHolder<?> recipe, int mouseX, int mouseY);

    @Override
    public void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, RECIPE_BACKGROUND, mouseX, mouseY, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        extractRecipeTooltip(graphics, recipe, mouseX, mouseY);
    }

    protected boolean hasEncountered(Item item) {
        for (GraphElement element : getFrom()) {
            if (element instanceof ItemElement itemElement) {
                if (itemElement.item.equals(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void extractIngredient(GuiGraphicsExtractor graphics, Ingredient ingredient, int x, int y, boolean filter) {
        List<Item> slotItems = ingredient.items()
                .sorted(Comparator.comparing(entry -> entry.unwrapKey().orElseThrow().identifier()))
                .map(Holder::value)
                .filter(item -> !filter || hasEncountered(item))
                .toList();
        Item item = Cycler.one(slotItems);
        if (item == null) return;

        graphics.fakeItem(item.getDefaultInstance(), x, y);
    }

    protected void extractOutput(GuiGraphicsExtractor graphics, int x, int y) {
        for(GraphElement element : getTo()) {
            if(element instanceof ItemElement itemElement) {
                graphics.fakeItem(itemElement.item.getDefaultInstance(), x, y);
                return;
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(station, recipe);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CraftingStationGraphElement element) {
            return station.equals(element.station) && recipe.equals(element.recipe);
        }
        return false;
    }

    @Override
    public String toString() {
        return "CraftingStationGraphElement[%s, %s]".formatted(station, recipe.id());
    }
}
