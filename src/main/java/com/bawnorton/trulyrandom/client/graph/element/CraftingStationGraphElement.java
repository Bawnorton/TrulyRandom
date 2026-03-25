package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.util.Cycler;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeHolder;
import net.minecraft.registry.entry.Holder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
    protected Text getTooltip() {
        return Text.translatable("container.crafting");
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int mouseX, int mouseY, int x, int y, float scale) {
        context.drawItemWithoutEntity(station.getDefaultInstance(), x - 8, y - 8);
    }

    @Override
    public void renderBackground(DrawContext context, int x, int y, int width, int height) {
        Identifier texture = AdvancementObtainedStatus.UNOBTAINED.getFrameTexture(AdvancementFrame.GOAL);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, x, y, width, height, isHovered() ? 0xFF6666FF : 0xFFAAAAFF);
    }

    protected abstract void renderRecipeTooltip(DrawContext context, RecipeHolder<?> recipe, int mouseX, int mouseY);

    @Override
    public void drawTooltip(DrawContext context, int mouseX, int mouseY) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, RECIPE_BACKGROUND, mouseX, mouseY, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        renderRecipeTooltip(context, recipe, mouseX, mouseY);
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

    protected void renderIngredient(DrawContext context, Ingredient ingredient, int x, int y, boolean filter) {
        List<Item> slotItems = ingredient.getMatchingItems()
                .sorted(Comparator.comparing(entry -> entry.getKey().orElseThrow().getValue()))
                .map(Holder::value)
                .filter(item -> !filter || hasEncountered(item))
                .toList();
        Item item = Cycler.one(slotItems);
        if (item == null) return;

        context.drawItem(item.getDefaultInstance(), x, y);
    }

    protected void renderOutput(DrawContext context, int x, int y) {
        for(GraphElement element : getTo()) {
            if(element instanceof ItemElement itemElement) {
                context.drawItem(itemElement.item.getDefaultInstance(), x, y);
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
