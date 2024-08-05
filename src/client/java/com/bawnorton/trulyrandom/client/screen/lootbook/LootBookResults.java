package com.bawnorton.trulyrandom.client.screen.lootbook;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LootBookResults {
    private static final ButtonTextures PAGE_FORWARD_TEXTURES = new ButtonTextures(
            Identifier.ofVanilla("recipe_book/page_forward"),
            Identifier.ofVanilla("recipe_book/page_forward_highlighted")
    );
    private static final ButtonTextures PAGE_BACKWARD_TEXTURES = new ButtonTextures(
            Identifier.ofVanilla("recipe_book/page_backward"),
            Identifier.ofVanilla("recipe_book/page_backward_highlighted")
    );

    private MinecraftClient client;

    private List<Item> drops;
    private final List<LootResultButton> resultButtons = new ArrayList<>(20);
    private LootResultButton hoveredResultButton;
    private Item lastClickedItem;
    private int pageCount;
    private int currentPage;
    private int y;

    private ToggleButtonWidget nextPageButton;
    private ToggleButtonWidget prevPageButton;

    private LootTableTracker lootTracker;

    public LootBookResults() {
        for(int i = 0; i < 20; i++) {
            resultButtons.add(new LootResultButton());
        }
    }

    public void initalize(MinecraftClient client, int parentRight, int parentTop) {
        this.client = client;
        y = parentTop;
        lootTracker = TrulyRandomClient.getRandomiser().getLootTableTracker();
        drops = lootTracker.getAllDrops();

        for (int i = 0; i < resultButtons.size(); i++) {
            LootResultButton resultButton = resultButtons.get(i);
            resultButton.setPosition(
                    parentRight + 11 + LootResultButton.BUTTON_SIZE * (i % 5),
                    y + 31 + LootResultButton.BUTTON_SIZE * (i / 5)
            );
        }

        nextPageButton = new ToggleButtonWidget(parentRight + 93, y + 137, 12, 17, false);
        nextPageButton.setTextures(PAGE_FORWARD_TEXTURES);
        prevPageButton = new ToggleButtonWidget(parentRight + 38, y + 137, 12, 17, true);
        prevPageButton.setTextures(PAGE_BACKWARD_TEXTURES);
    }

    public void clearHovered() {
        hoveredResultButton = null;
    }

    public void setResults(List<Item> lootTables, boolean resetCurrentPage) {
        this.drops = lootTables;
        pageCount = (int) Math.ceil(lootTables.size() / 20.0);
        if(pageCount <= currentPage || resetCurrentPage) {
            currentPage = 0;
        }

        refreshResultButtons();
    }

    private void refreshResultButtons() {
        int previousButtons = 20 * currentPage;
        for(int i = 0; i < resultButtons.size(); i++) {
            LootResultButton resultButton = resultButtons.get(i);
            if(i + previousButtons < drops.size()) {
                Item drop = drops.get(i + previousButtons);
                resultButton.showDrop(drop);
                resultButton.visible = true;
            } else {
                resultButton.visible = false;
            }
        }
        hideShowPageButtons();
    }

    private void hideShowPageButtons() {
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

    public void draw(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
        if(pageCount > 1) {
            Text text = Text.translatable("gui.recipebook.page", currentPage + 1, pageCount);
            int textWidth = client.textRenderer.getWidth(text);
            context.drawText(client.textRenderer, text, x - textWidth / 2 + 73, y + 141, Colors.WHITE, false);
        }

        hoveredResultButton = null;

        for(LootResultButton resultButton : resultButtons) {
            resultButton.render(context, mouseX, mouseY, delta);
            if(resultButton.visible && resultButton.isSelected()) {
                hoveredResultButton = resultButton;
            }
        }

        prevPageButton.render(context, mouseX, mouseY, delta);
        nextPageButton.render(context, mouseX, mouseY, delta);
    }

    public void drawTooltip(DrawContext context, int x, int y) {
        if(client.currentScreen != null && hoveredResultButton != null) {
            context.drawOrderedTooltip(client.textRenderer, hoveredResultButton.getTooltip().getLines(client), x, y);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(nextPageButton.mouseClicked(mouseX, mouseY, button)) {
            currentPage++;
            refreshResultButtons();
            return true;
        }

        if(prevPageButton.mouseClicked(mouseX, mouseY, button)) {
            currentPage--;
            refreshResultButtons();
            return true;
        }

        for(LootResultButton resultButton : resultButtons) {
            if(resultButton.mouseClicked(mouseX, mouseY, button)) {
                if(button == 0) {
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

    public List<Item> getAllDrops() {
        return lootTracker.getAllDrops().stream().sorted(Comparator.comparing(item -> item.getName().getString())).toList();
    }
}
