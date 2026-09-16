package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.util.Cycler;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.joml.Matrix3x2fStack;

import java.util.*;

public abstract class CraftingStationGraphElement extends GraphElement {
    protected static final Identifier RECIPE_BACKGROUND = TrulyRandom.id("loot_book/background");
    protected static final int BACKGROUND_WIDTH = 150;
    protected static final int BACKGROUND_HEIGHT = 66;

    private final RecipeHolder<?> recipe;
    private final Item station;
    private final Long2ObjectMap<Cycler<Item>> cyclers = new Long2ObjectOpenHashMap<>();

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
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.scale(0.75F, 0.75F);
        mouseX = (int) (mouseX / 0.75F);
        mouseY = (int) (mouseY / 0.75F);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, RECIPE_BACKGROUND, mouseX, mouseY, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        extractRecipeTooltip(graphics, recipe, mouseX, mouseY);
        pose.popMatrix();
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
        List<Item> items = ingredient.items()
                .sorted(Comparator.comparing(entry -> entry.unwrapKey().map(ResourceKey::identifier).orElse(TrulyRandom.id("missing"))))
                .map(Holder::value)
                .filter(item -> !filter || hasEncountered(item))
                .toList();

        Cycler<Item> cycler = cyclers.computeIfAbsent(items.hashCode(), _ -> new Cycler<>(items));
        Item item = cycler.current();
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
