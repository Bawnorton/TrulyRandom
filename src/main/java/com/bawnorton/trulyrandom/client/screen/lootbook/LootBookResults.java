package com.bawnorton.trulyrandom.client.screen.lootbook;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.random.ClientRandomiser;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LootBookResults {
    private static final WidgetSprites PAGE_FORWARD_TEXTURES = new WidgetSprites(
            Identifier.withDefaultNamespace("recipe_book/page_forward"),
            Identifier.withDefaultNamespace("recipe_book/page_forward_highlighted")
    );
    private static final WidgetSprites PAGE_BACKWARD_TEXTURES = new WidgetSprites(
            Identifier.withDefaultNamespace("recipe_book/page_backward"),
            Identifier.withDefaultNamespace("recipe_book/page_backward_highlighted")
    );

    private static final Component NEXT_PAGE_TEXT = Component.translatable("gui.recipebook.next_page");
    private static final Component PREVIOUS_PAGE_TEXT = Component.translatable("gui.recipebook.previous_page");

    private Minecraft minecraft;

    private List<Item> items;
    private final List<LootResultButton> resultButtons = new ArrayList<>(20);
    private LootResultButton hoveredResultButton;
    private Item lastClickedItem;
    private int pageCount;
    private int currentPage;
    private int y;

    private ImageButton nextPageButton;
    private ImageButton prevPageButton;

    private LootTableTracker lootTracker;
    private RecipeTracker recipeTracker;

    public LootBookResults() {
        for(int i = 0; i < 20; i++) {
            resultButtons.add(new LootResultButton());
        }
    }

    public void initalize(Minecraft minecraft, int parentRight, int parentTop) {
        this.minecraft = minecraft;
        y = parentTop;
        refreshTrackers();
        items = getAllItems();

        for (int i = 0; i < resultButtons.size(); i++) {
            LootResultButton resultButton = resultButtons.get(i);
            resultButton.setPosition(
                    parentRight + 11 + LootResultButton.BUTTON_SIZE * (i % 5),
                    y + 31 + LootResultButton.BUTTON_SIZE * (i / 5)
            );
        }

        nextPageButton = new ImageButton(parentRight + 93, y + 137, 12, 17, PAGE_FORWARD_TEXTURES, _ -> updateArrowButons(), NEXT_PAGE_TEXT);
        prevPageButton = new ImageButton(parentRight + 38, y + 137, 12, 17, PAGE_BACKWARD_TEXTURES, _ -> updateArrowButons(), PREVIOUS_PAGE_TEXT);
    }

    public void refreshTrackers() {
        ClientRandomiser clientRandomiser = TrulyRandomClient.getRandomiser();
        lootTracker = clientRandomiser.getLootTableTracker();
        recipeTracker = clientRandomiser.getRecipeTracker();
    }

    public void clearHovered() {
        hoveredResultButton = null;
    }

    public void setResults(List<Item> items, boolean resetCurrentPage) {
        this.items = items;
        pageCount = (int) Math.ceil(items.size() / 20.0);
        if(pageCount <= currentPage || resetCurrentPage) {
            currentPage = 0;
        }

        refreshResultButtons();
    }

    private void refreshResultButtons() {
        int previousButtons = 20 * currentPage;
        for(int i = 0; i < resultButtons.size(); i++) {
            LootResultButton resultButton = resultButtons.get(i);
            if(i + previousButtons < items.size()) {
                Item drop = items.get(i + previousButtons);
                resultButton.showDrop(drop);
                resultButton.visible = true;
            } else {
                resultButton.visible = false;
            }
        }
        updateArrowButons();
    }

    private void updateArrowButons() {
        nextPageButton.visible = pageCount > 1 && currentPage < pageCount - 1;
        prevPageButton.visible = pageCount > 1 && currentPage > 0;
    }

    public void setY(int y) {
        this.y = y;
        nextPageButton.setY(y + 137);
        prevPageButton.setY(y + 137);
        for (int i = 0; i < resultButtons.size(); i++) {
            LootResultButton resultButton = resultButtons.get(i);
            resultButton.setY(
                    y + 31 + LootResultButton.BUTTON_SIZE * (i / 5)
            );
        }
    }

    public int getY() {
        return y;
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY, float a) {
        if(pageCount > 1) {
            Component text = Component.translatable("gui.recipebook.page", currentPage + 1, pageCount);
            int textWidth = minecraft.font.width(text);
            graphics.text(minecraft.font, text, x - textWidth / 2 + 73, y + 141, CommonColors.WHITE, false);
        }

        hoveredResultButton = null;

        for(LootResultButton resultButton : resultButtons) {
            resultButton.extractRenderState(graphics, mouseX, mouseY, a);
            if(resultButton.visible && resultButton.isFocused()) {
                hoveredResultButton = resultButton;
            }
        }

        prevPageButton.extractRenderState(graphics, mouseX, mouseY, a);
        nextPageButton.extractRenderState(graphics, mouseX, mouseY, a);
    }

    public void extractTooltip(GuiGraphicsExtractor extractor, int x, int y) {
        if(minecraft.screen != null && hoveredResultButton != null) {
            extractor.setTooltipForNextFrame(minecraft.font, hoveredResultButton.getTooltip().toCharSequence(minecraft), x, y);
        }
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        lastClickedItem = null;
        if(nextPageButton.mouseClicked(event, doubleClick)) {
            currentPage++;
            refreshResultButtons();
            return true;
        }

        if(prevPageButton.mouseClicked(event, doubleClick)) {
            currentPage--;
            refreshResultButtons();
            return true;
        }

        for(LootResultButton resultButton : resultButtons) {
            if(resultButton.mouseClicked(event, doubleClick)) {
                if(event.isLeft()) {
                    lastClickedItem = resultButton.getDrop();
                    return true;
                }
            }
        }
        return false;
    }

    public Item getLastClickedItem() {
        return lastClickedItem;
    }

    public List<Item> getAllItems() {
        Set<Item> allItems = new HashSet<>(lootTracker.getAllDrops());
        allItems.addAll(recipeTracker.getAllOutputs());
        return allItems.stream()
                .sorted(Comparator.comparing(item -> item.getDefaultInstance().getDisplayName().getString()))
                .toList();
    }
}
