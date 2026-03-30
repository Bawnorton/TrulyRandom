package com.bawnorton.trulyrandom.client.screen.lootbook;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.MinecraftClientExtender;
import com.bawnorton.trulyrandom.client.graph.TrackingGraphBookController;
import com.bawnorton.trulyrandom.client.mixin.accessor.RecipeBookComponentAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class LootBookWidget implements Renderable, GuiEventListener, NarratableEntry {
    public static final WidgetSprites BUTTON_TEXTURES = new WidgetSprites(
            TrulyRandom.id("loot_book/button"),
            TrulyRandom.id("loot_book/button_focused")
    );
    private static final Identifier BACKGROUND_TEXTURE = RecipeBookComponentAccessor.trulyrandom$RECIPE_BOOK_LOCATION();

    public int topOffset;
    private int rightOffset;
    private int parentWidth;
    private int parentHeight;

    private Minecraft minecraft;
    private EditBox searchField;

    private final LootBookResults lootArea = new LootBookResults();
    private final LootBookGraph graph = new LootBookGraph();
    private final TrackingGraphBookController controller = TrulyRandomClient.getLootBookController();
    private final List<Consumer<LootBookGraph>> graphOpenListeners = new ArrayList<>();
    private final List<Consumer<LootBookGraph>> graphCloseListeners = new ArrayList<>();

    private boolean searching;
    private boolean open;
    private boolean narrow;
    private boolean isShort;
    private String searchedText;

    public void initialize(int parentWidth, int parentHeight, Minecraft minecraft, boolean narrow, boolean isShort) {
        this.minecraft = minecraft;
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
        String search = searchField == null ? "" : searchField.getValue();
        searchField = new EditBox(minecraft.font, x + 25, y + 13 + topOffset, 81, minecraft.font.lineHeight + 5, Component.translatable("itemGroup.trulyrandom.search"));
        searchField.setMaxLength(50);
        searchField.setVisible(true);
        searchField.setTextColor(-1);
        searchField.setValue(search);
        searchField.setHint(Component.translatable("gui.recipebook.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
        lootArea.initalize(minecraft, x, y + topOffset);
        graph.initalize(minecraft, controller, this, x - LootBookGraph.WIDTH / 2 - 16, y - topOffset);
        graph.show(controller.getGraphItem());
        if(((MinecraftClientExtender) minecraft).trulyrandom$isResizing()) {
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
        String searched = searchField.getValue().toLowerCase(Locale.ENGLISH);
        if(!searched.equals(searchedText)) {
            refreshResults();
            searchedText = searched;
        }
    }

    public void refreshResults() {
        lootArea.refreshTrackers();
        List<Item> items = new ArrayList<>(lootArea.getAllItems());
        String search = searchField.getValue();
        if(!search.isEmpty()) {
            items = items.stream()
                    .filter(drop -> {
                        String name = drop.getDefaultInstance().getHoverName().getString();
                        String transformed = name.toLowerCase();
                        return transformed.contains(search.toLowerCase());
                    })
                    .toList();
        }

        lootArea.setResults(items, false);
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
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if(!open) return;

        if(!(isShort && isGraphOpen())) {
            int x = (parentWidth - 147) / 2 + rightOffset;
            int y = (parentHeight - 166) / 2 + topOffset;
            graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x, y, 1.0F, 1.0F, 147, 166, 256, 256);
            searchField.extractRenderState(graphics, mouseX, mouseY, a);
            lootArea.extractRenderState(graphics, x, y, mouseX, mouseY, a);
        }
        extractGraphRenderState(graphics, mouseX, mouseY, a);
    }

    public void extractGraphRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graph.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    public void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if(!isOpen()) return;

        graph.extractTooltip(graphics, mouseX, mouseY);
        lootArea.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!isOpen() || minecraft.player.isSpectator()) return false;

        if(graph.mouseClicked(event, doubleClick)) {
            return true;
        }

        if(lootArea.mouseClicked(event, doubleClick)) {
            if(lootArea.getLastClickedItem() != null) {
                openGraph(lootArea.getLastClickedItem());
            }
            return true;
        }

        if(searchField.mouseClicked(event, doubleClick)) {
            searchField.setFocused(true);
            return true;
        }

        searchField.setFocused(false);
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        return graph.mouseDragged(event, dx, dy);
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
    public boolean keyPressed(KeyEvent keyEvent) {
        searching = false;
        if (!isOpen() || minecraft.player.isSpectator()) return false;

        int keyCode = keyEvent.key();
        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if(narrow) {
                setOpen(false);
                return true;
            } else if (isShort && isGraphOpen()) {
                closeGraph();
                return true;
            }
        }

        if(searchField.keyPressed(keyEvent)) {
            refreshSearchResults();
            return true;
        }

        if(searchField.isFocused() && searchField.isVisible() && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }

        if(minecraft.options.keyChat.matches(keyEvent) && searchField.isFocused()) {
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
    public boolean keyReleased(KeyEvent event) {
        searching = false;
        return GuiEventListener.super.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if(searching) return false;
        if(!isOpen() || minecraft.player.isSpectator()) return false;
        if(searchField.charTyped(event)) {
            refreshSearchResults();
            return true;
        }

        return GuiEventListener.super.charTyped(event);
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {
    }
}
