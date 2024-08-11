package com.bawnorton.trulyrandom.client.screen.lootbook;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.InventoryScreenExtender;
import com.bawnorton.trulyrandom.client.extend.MinecraftClientExtender;
import com.bawnorton.trulyrandom.client.loot.LootBookController;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class LootBookWidget implements Drawable, Element, Selectable {
    public static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(
            TrulyRandom.id("loot_book/button"),
            TrulyRandom.id("loot_book/button_focused")
    );
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("minecraft", "textures/gui/recipe_book.png");

    public int topOffset;
    private int rightOffset;
    private int parentWidth;
    private int parentHeight;

    private MinecraftClient client;
    private TextFieldWidget searchField;

    private final LootBookResults lootArea = new LootBookResults();
    private final LootBookGraph graph = new LootBookGraph();
    private final LootBookController controller = TrulyRandomClient.getLootBookController();
    private final List<Consumer<LootBookGraph>> graphOpenListeners = new ArrayList<>();
    private final List<Consumer<LootBookGraph>> graphCloseListeners = new ArrayList<>();

    private boolean searching;
    private boolean open;
    private boolean narrow;
    private boolean isShort;
    private String searchedText;

    public void initialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow, boolean isShort) {
        this.client = client;
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.narrow = narrow;
        this.isShort = isShort;

        open = controller.isLootBookOpen();

        if(open) {
            reset();
        }
    }

    public void reset() {
        rightOffset = narrow ? 0 : 86;
        if(controller.getGraphItem() != null) {
            topOffset = isShort ? 0 : LootBookGraph.HEIGHT / 2 + 2;
        } else {
            topOffset = 0;
        }
        int x = (parentWidth - 147) / 2 + rightOffset;
        int y = (parentHeight - 166) / 2;
        String search = searchField == null ? "" : searchField.getText();
        searchField = new TextFieldWidget(client.textRenderer, x + 25, y + 13 + topOffset, 81, client.textRenderer.fontHeight + 5, Text.translatable("itemGroup.trulyrandom.search"));
        searchField.setMaxLength(50);
        searchField.setVisible(true);
        searchField.setEditableColor(16777215);
        searchField.setText(search);
        searchField.setPlaceholder(Text.translatable("gui.recipebook.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
        lootArea.initalize(client, x, y + topOffset);
        graph.initalize(client, controller, this, x - LootBookGraph.WIDTH / 2 - 16, y - topOffset);
        graph.show(controller.getGraphItem());
        if(((MinecraftClientExtender) client).trulyrandom$isResizing()) {
            graph.moveToRoot();
        }
        refreshResults();
    }

    public void registerGraphListener(Consumer<LootBookGraph> openListener, Consumer<LootBookGraph> closeListener) {
        graphOpenListeners.add(openListener);
        graphCloseListeners.add(closeListener);
    }

    public int findLeftEdge(int width, int backgroundWidth) {
        int edge = (width - backgroundWidth) / 2;
        if(isOpen() && !narrow) {
            edge -= 77;
        }
        return edge;
    }

    public void toggleOpen() {
        setOpen(!isOpen());
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean opened) {
        if(opened) {
            reset();
        } else if (graph.isOpen()) {
            closeGraph();
        }
        controller.setLootBookOpen(opened);
        open = opened;
    }

    private void refreshSearchResults() {
        String searched = searchField.getText().toLowerCase(Locale.ENGLISH);
        if(!searched.equals(searchedText)) {
            refreshResults();
            searchedText = searched;
        }
    }

    private void refreshResults() {
        List<Item> drops = new ArrayList<>(lootArea.getAllDrops());
        String search = searchField.getText();
        if(!search.isEmpty()) {
            drops = drops.stream()
                    .filter(drop -> {
                        String name = drop.getName().getString();
                        String transformed = name.toLowerCase();
                        return transformed.contains(search.toLowerCase());
                    })
                    .toList();
        }

        lootArea.setResults(drops, false);
    }

    private void openGraph(Item lastClickedItem) {
        if (!graph.isOpen()) {
            lootArea.clearHovered();

            topOffset = isShort ? 0 : LootBookGraph.HEIGHT / 2 + 2;
            graph.setY(graph.getY() - topOffset);
            lootArea.setY(lootArea.getY() + topOffset);
            searchField.setY(searchField.getY() + topOffset);
            graphOpenListeners.forEach(c -> c.accept(graph));
        }

        graph.show(lastClickedItem);
    }

    public void closeGraph() {
        if(graph.isOpen()) {
            graph.setY(graph.getY() + topOffset);
            lootArea.setY(lootArea.getY() - topOffset);
            searchField.setY(searchField.getY() - topOffset);
            topOffset = 0;
            graphCloseListeners.forEach(c -> c.accept(graph));
        }

        graph.hide();
    }

    public boolean isGraphOpen() {
        return isOpen() && graph.isOpen();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if(!open) return;

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 100F);
        if(!(isShort && isGraphOpen())) {
            int x = (parentWidth - 147) / 2 + rightOffset;
            int y = (parentHeight - 166) / 2 + topOffset;
            context.drawTexture(BACKGROUND_TEXTURE, x, y, 1, 1, 147, 166);
            searchField.render(context, mouseX, mouseY, delta);
            lootArea.draw(context, x, y, mouseX, mouseY, delta);
        }
        renderGraph(context, mouseX, mouseY, delta);
        context.getMatrices().pop();
    }

    public void renderGraph(DrawContext context, int mouseX, int mouseY, float delta) {
        graph.render(context, mouseX, mouseY, delta);
    }

    public void drawTooltip(DrawContext context, int mouseX, int mouseY) {
        if(!isOpen()) return;

        graph.drawTooltip(context, mouseX, mouseY);
        lootArea.drawTooltip(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isOpen() || client.player.isSpectator()) return false;

        if(graph.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if(lootArea.mouseClicked(mouseX, mouseY, button)) {
            if(lootArea.getLastClickedItem() != null) {
                openGraph(lootArea.getLastClickedItem());
            }
            return true;
        }

        if(searchField.mouseClicked(mouseX, mouseY, button)) {
            searchField.setFocused(true);
            return true;
        }

        searchField.setFocused(false);
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return graph.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return graph.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return graph.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        searching = false;
        if (!isOpen() || client.player.isSpectator()) return false;

        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if(narrow) {
                setOpen(false);
                return true;
            } else if (isShort && isGraphOpen()) {
                closeGraph();
                return true;
            }
        }

        if(searchField.keyPressed(keyCode, scanCode, modifiers)) {
            refreshSearchResults();
            return true;
        }

        if(searchField.isFocused() && searchField.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }

        if(client.options.chatKey.matchesKey(keyCode, scanCode) && searchField.isFocused()) {
            searching = true;
            searchField.setFocused(true);
            return true;
        }

        return false;
    }

    public boolean isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int backgroundWidth, int backgroundHeight) {
        if(!isOpen()) return true;

        boolean outside = mouseX < x || mouseY < y || mouseX >= x + backgroundWidth || mouseY >= y + backgroundHeight;
        boolean inside = x + backgroundWidth < mouseX && mouseX < x + backgroundWidth + 147 && y < mouseY && mouseY < y + backgroundHeight;
        return outside && !inside;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        searching = false;
        return Element.super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if(searching) return false;
        if(!isOpen() || client.player.isSpectator()) return false;
        if(searchField.charTyped(chr, modifiers)) {
            refreshSearchResults();
            return true;
        }

        return Element.super.charTyped(chr, modifiers);
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }
}
