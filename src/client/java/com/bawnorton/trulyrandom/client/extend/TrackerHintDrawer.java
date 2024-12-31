package com.bawnorton.trulyrandom.client.extend;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public interface TrackerHintDrawer {
    Identifier UNBROKEN = TrulyRandom.id("loot_tracker/unbroken");
    Identifier NOT_SILKED = TrulyRandom.id("loot_tracker/not_silked");

    default void drawHints(DrawContext context, int x, int y, Item item) {
        LootTableTracker tracker = TrulyRandomClient.getRandomiser().getLootTableTracker();
        boolean knowsLootTable = tracker.knowsItemLootTable(item);
        boolean brokeWithSilk = tracker.brokeWithSilk(item);
        if(knowsLootTable && !brokeWithSilk) {
            context.drawGuiTexture(RenderLayer::getGuiTextured, NOT_SILKED, x, y, 16, 16);
        } else if (!knowsLootTable) {
            context.drawGuiTexture(RenderLayer::getGuiTextured, UNBROKEN, x, y, 16, 16);
        }
    }
}
