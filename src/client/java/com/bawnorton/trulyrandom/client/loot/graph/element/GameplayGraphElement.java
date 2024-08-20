package com.bawnorton.trulyrandom.client.loot.graph.element;

import com.bawnorton.trulyrandom.client.mixin.accessor.CatEntityAccessor;
import com.bawnorton.trulyrandom.client.screen.LivingEntityGuiRenderer;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class GameplayGraphElement extends IdBasedGraphElement implements LivingEntityGuiRenderer {
    public GameplayGraphElement(LootTableIdentifier lootTableId) {
        super(lootTableId);
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int x, int y, float scale) {
        if(lootTableId.isFishing()) {
            context.drawItemWithoutEntity(Items.FISHING_ROD.getDefaultStack(), x - 8, y - 8);
        } else if (lootTableId.isPandaSneeze()) {
            PandaEntity panda = EntityType.PANDA.create(client.world);
            if(panda == null) return;

            panda.setSneezing(true);
            render(panda, client, x - 3, y, scale);
            context.getMatrices().push();
            context.getMatrices().translate(0, 0, 300);
            context.getMatrices().scale(0.5F, 0.5F, 1);
            x = (int) (x / 0.5);
            y = (int) (y / 0.5);
            x += 8;
            y += 8;
            context.drawItemWithoutEntity(Items.SLIME_BALL.getDefaultStack(), x, y);
            context.getMatrices().pop();
        } else if (lootTableId.isCatMorningGift()) {
            CatEntity cat = EntityType.CAT.create(client.world);
            if(cat == null) return;

            cat.setInSleepingPose(true);
            ((CatEntityAccessor) cat).setSleepAnimation(1);
            render(cat, client, x - 3, y, scale);
        } else if (lootTableId.isSnifferDigging()) {
            SnifferEntity sniffer = EntityType.SNIFFER.create(client.world);
            if(sniffer == null) return;

            sniffer.startState(SnifferEntity.State.DIGGING);
            sniffer.age += 60;
            render(sniffer, client, x, y, scale);
        } else if (lootTableId.isPiglinBartering()) {
            PiglinEntity piglin = EntityType.PIGLIN.create(client.world);
            if(piglin == null) return;

            piglin.equipStack(EquipmentSlot.OFFHAND, Items.GOLD_INGOT.getDefaultStack());
            render(piglin, client, x, y, scale);
        }
    }

    @Override
    protected Text getTooltip() {
        if(lootTableId.isFishing()) {
            return Text.of("Fishing");
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
