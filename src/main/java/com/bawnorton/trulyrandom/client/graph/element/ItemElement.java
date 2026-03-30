package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ItemElement extends GraphElement {
    protected final Item item;
    private int counter = 0;
    private int offset = 0;

    public ItemElement(Item item) {
        this.item = item;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        graphics.fakeItem(item.getDefaultInstance(), x - 8, y - 8);
    }

    @Override
    protected Component getTooltip() {
        Component tooltip = item.getDefaultInstance().getHoverName();
        Minecraft minecraft = Minecraft.getInstance();

        if(!minecraft.hasShiftDown()) return tooltip;

        Player player = minecraft.player;
        if(player == null) return tooltip;

        Set<LootTableDrops> sources = TrulyRandomClient.getRandomiser().getLootTableTracker().getSources(item);
        if(sources.isEmpty()) return tooltip;

        List<String> lootTables = sources.stream()
                .map(LootTableDrops::getLootTableId)
                .map(LootTableIdentifier::toString)
                .sorted(String::compareTo)
                .toList();

        counter++;
        if(counter >= minecraft.getFps()) {
            counter = 0;
            offset++;
            if(offset >= lootTables.size()) {
                offset = 0;
            }
        }

        return Component.literal("%s (%s/%s)".formatted(lootTables.get(offset), offset + 1, lootTables.size()));
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemElement that)) return false;
        return Objects.equals(item, that.item);
    }

    @Override
    public String toString() {
        return "ItemElement[%s]".formatted(item.toString());
    }
}
