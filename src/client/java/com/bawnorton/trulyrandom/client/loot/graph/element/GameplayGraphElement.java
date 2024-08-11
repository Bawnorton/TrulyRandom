package com.bawnorton.trulyrandom.client.loot.graph.element;

import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

public class GameplayGraphElement extends IdBasedGraphElement {
    public GameplayGraphElement(LootTableIdentifier lootTableId) {
        super(lootTableId);
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int x, int y, float scale) {
        if(lootTableId.isHeroOfTheVillage()) {
            String gift = lootTableId.getSegments()[2];
            String villager = gift.substring(0, gift.lastIndexOf("_"));

        }
        context.drawItemWithoutEntity(Items.DIAMOND_SWORD.getDefaultStack(), x - 8, y - 8);
    }

    @Override
    protected Text getTooltip() {
        if(lootTableId.isFishing()) {
            return Text.of("Fishing");
        } else if (lootTableId.isHeroOfTheVillage()) {
            return Text.of("Hero of the Village");
        } else if (lootTableId.isCatMorningGift()) {
            return Text.of("Cat Morning Gift");
        } else if (lootTableId.isPandaSneeze()) {
            return Text.of("Panda Sneeze");
        } else if (lootTableId.isPiglinBartering()) {
            return Text.of("Piglin Bartering");
        } else if (lootTableId.isSnifferDigging()) {
            return Text.of("Sniffer Digging");
        }
        return super.getTooltip();
    }
}
