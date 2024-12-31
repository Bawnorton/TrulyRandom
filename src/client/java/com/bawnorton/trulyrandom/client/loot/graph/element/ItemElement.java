package com.bawnorton.trulyrandom.client.loot.graph.element;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import java.util.List;
import java.util.Set;

public class ItemElement extends GraphElement {
    private final Item item;
    private int counter = 0;
    private int offset = 0;

    public ItemElement(Item item) {
        this.item = item;
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int mouseX, int mouseY, int x, int y, float scale) {
        context.drawItemWithoutEntity(item.getDefaultStack(), x - 8, y - 8);
    }

    @Override
    protected Text getTooltip() {
        Text tooltip = item.getName();
        if(!Screen.hasShiftDown()) return tooltip;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if(player == null) return tooltip;

        Set<LootTableDrops> sources = TrulyRandomClient.getRandomiser().getLootTableTracker().getSources(item);
        if(sources.isEmpty()) return tooltip;

        List<String> lootTables = sources.stream()
                .map(LootTableDrops::getLootTableId)
                .map(LootTableIdentifier::toString)
                .sorted(String::compareTo)
                .toList();

        counter++;
        if(counter >= client.getCurrentFps()) {
            counter = 0;
            offset++;
            if(offset >= lootTables.size()) {
                offset = 0;
            }
        }

        return Text.of("%s (%s/%s)".formatted(lootTables.get(offset), offset + 1, lootTables.size()));
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemElement itemElement) {
            return itemElement.item.equals(item);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ItemElement[%s]".formatted(item.toString());
    }
}
