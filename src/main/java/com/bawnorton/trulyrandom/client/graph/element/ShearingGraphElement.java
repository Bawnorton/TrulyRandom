package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.mixin.accessor.MushroomCowAccessor;
import com.bawnorton.trulyrandom.client.screen.render.LivingEntityGuiRenderer;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.monster.skeleton.Bogged;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix3x2fStack;

public class ShearingGraphElement extends IdBasedGraphElement implements LivingEntityGuiRenderer {
    public ShearingGraphElement(LootTableIdentifier lootTableId) {
        super(lootTableId);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        if(lootTableId.isBoggedShearing()) {
            Bogged bogged = EntityType.BOGGED.create(minecraft.level, EntitySpawnReason.COMMAND);
            if(bogged == null) return;

            extractRenderState(graphics, mouseX, mouseY, bogged, x - 5, y, scale);
        } else if (lootTableId.isMooshroomShearing()) {
            MushroomCow mushroomCow = EntityType.MOOSHROOM.create(minecraft.level, EntitySpawnReason.COMMAND);
            if (mushroomCow == null) return;

            if(lootTableId.isRedMooshroomShearing()) {
                ((MushroomCowAccessor) mushroomCow).trulyrandom$setVariant(MushroomCow.Variant.RED);
            } else if (lootTableId.isBrownMooshroomShearing()) {
                ((MushroomCowAccessor) mushroomCow).trulyrandom$setVariant(MushroomCow.Variant.BROWN);
            }
            extractRenderState(graphics, mouseX, mouseY, mushroomCow, x - 5, y, scale);
        } else if (lootTableId.isSnowGolemShearing()) {
            SnowGolem snowGolem = EntityType.SNOW_GOLEM.create(minecraft.level, EntitySpawnReason.COMMAND);
            if (snowGolem == null) return;

            snowGolem.setPumpkin(false);
            extractRenderState(graphics, mouseX, mouseY, snowGolem, x - 5, y, scale);
        } else if (lootTableId.isSheepShearing()) {
            Sheep sheep = EntityType.SHEEP.create(minecraft.level, EntitySpawnReason.COMMAND);
            if (sheep == null) return;

            sheep.setSheared(true);
            if(lootTableId.isColouredSheepShearing()) {
                sheep.setColor(lootTableId.getDyeColourForSheepShearing());
            }
            extractRenderState(graphics, mouseX, mouseY, sheep, x - 5, y, scale);
        }
        Matrix3x2fStack matrices = graphics.pose();
        matrices.pushMatrix();
        matrices.scale(0.7F, 0.7F);
        x = (int) (x / 0.7F);
        y = (int) (y / 0.7F);
        graphics.fakeItem(Items.SHEARS.getDefaultInstance(), x - 1, y - 8);
        matrices.popMatrix();
    }

    @Override
    public int getColour() {
        return CommonColors.HIGH_CONTRAST_DIAMOND;
    }

    @Override
    protected Component getTooltip() {
        if (lootTableId.isBoggedShearing()) {
            return Component.literal("Shearing Bogged");
        } else if (lootTableId.isMooshroomShearing()) {
            if (lootTableId.isRedMooshroomShearing()) {
                return Component.literal("Shearing Red Mooshroom");
            } else if (lootTableId.isBrownMooshroomShearing()) {
                return Component.literal("Shearing Brown Mooshroom");
            }
            return Component.literal("Shearing Mooshroom");
        } else if (lootTableId.isSnowGolemShearing()) {
            return Component.literal("Shearing Snow Golem");
        } else if (lootTableId.isSheepShearing()) {
            if(lootTableId.isColouredSheepShearing()) {
                return Component.literal("Shearing %s Sheep".formatted(StringUtils.capitalize(lootTableId.getDyeColourForSheepShearing().getName())));
            }
            return Component.literal("Shearing Sheep");
        }
        return super.getTooltip();
    }
}
