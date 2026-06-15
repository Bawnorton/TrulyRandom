package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.mixin.accessor.CatEntityAccessor;
import com.bawnorton.trulyrandom.client.screen.render.LivingEntityGuiRenderer;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.Items;
import org.joml.Matrix3x2fStack;

public class GameplayGraphElement extends IdBasedGraphElement implements LivingEntityGuiRenderer {
    public GameplayGraphElement(LootTableIdentifier lootTableId) {
        super(lootTableId);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        if(lootTableId.isFishing()) {
            graphics.fakeItem(Items.FISHING_ROD.getDefaultInstance(), x - 8, y - 8);
        } else if (lootTableId.isPandaSneeze()) {
            Panda panda = EntityType.PANDA.create(minecraft.level, EntitySpawnReason.COMMAND);
            if(panda == null) return;

            panda.sneeze(true);
            extractRenderState(graphics, mouseX, mouseY, panda, x - 3, y, scale);
            Matrix3x2fStack matrices = graphics.pose();
            matrices.pushMatrix();
            matrices.scale(0.5F, 0.5F);
            x = (int) (x / 0.5);
            y = (int) (y / 0.5);
            x += 8;
            y += 8;
            graphics.fakeItem(Items.SLIME_BALL.getDefaultInstance(), x, y);
            matrices.popMatrix();
        } else if (lootTableId.isCatMorningGift()) {
            Cat cat = EntityType.CAT.create(minecraft.level, EntitySpawnReason.COMMAND);
            if(cat == null) return;

            cat.setLying(true);
            ((CatEntityAccessor) cat).trulyrandom$lieDownAmount(1);
            extractRenderState(graphics, mouseX, mouseY, cat, x, y, scale);
        } else if (lootTableId.isSnifferDigging()) {
            Sniffer sniffer = EntityType.SNIFFER.create(minecraft.level, EntitySpawnReason.COMMAND);
            if(sniffer == null) return;

            sniffer.transitionTo(Sniffer.State.DIGGING);
            sniffer.tickCount += 60;
            extractRenderState(graphics, mouseX, mouseY, sniffer, x, y, scale);
        } else if (lootTableId.isPiglinBartering()) {
            Piglin piglin = EntityType.PIGLIN.create(minecraft.level, EntitySpawnReason.COMMAND);
            if(piglin == null) return;

            piglin.setItemSlot(EquipmentSlot.OFFHAND, Items.GOLD_INGOT.getDefaultInstance());
            extractRenderState(graphics, mouseX, mouseY, piglin, x, y, scale);
        } else if (lootTableId.isChickenLay()) {
            Chicken chicken = EntityType.CHICKEN.create(minecraft.level, EntitySpawnReason.COMMAND);
            if(chicken == null) return;

            extractRenderState(graphics, mouseX, mouseY, chicken, x, y, scale);
        } else if (lootTableId.isArmadilloShed()) {
            Armadillo armadillo = EntityType.ARMADILLO.create(minecraft.level, EntitySpawnReason.COMMAND);
            if(armadillo == null) return;

            extractRenderState(graphics, mouseX, mouseY, armadillo, x, y, scale);
        } else if (lootTableId.isTurtleGrow()) {
            Turtle turtle = EntityType.TURTLE.create(minecraft.level, EntitySpawnReason.COMMAND);
            if(turtle == null) return;

            turtle.setBaby(true);
            extractRenderState(graphics, mouseX, mouseY, turtle, x, y, scale);
        }
    }

    @Override
    public int getColour() {
        return CommonColors.HIGH_CONTRAST_DIAMOND;
    }

    @Override
    protected Component getTooltip() {
        if(lootTableId.isFishing()) {
            return Component.literal("Fishing");
        } else if (lootTableId.isCatMorningGift()) {
            return Component.literal("Cat Morning Gift");
        } else if (lootTableId.isPandaSneeze()) {
            return Component.literal("Panda Sneeze");
        } else if (lootTableId.isPiglinBartering()) {
            return Component.literal("Piglin Bartering");
        } else if (lootTableId.isSnifferDigging()) {
            return Component.literal("Sniffer Digging");
        } else if (lootTableId.isChickenLay()) {
            return Component.literal("Chicken Laying Egg");
        } else if (lootTableId.isArmadilloShed()) {
            return Component.literal("Armadillo Shedding");
        } else if (lootTableId.isTurtleGrow()) {
            return Component.literal("Baby Turtle Growing");
        }
        return super.getTooltip();
    }
}
