package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.screen.LivingEntityGuiRenderer;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.item.Items;

public class BrushGraphElement extends IdBasedGraphElement implements LivingEntityGuiRenderer {
    public BrushGraphElement(LootTableIdentifier lootTableId) {
        super(lootTableId);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        if(lootTableId.isArmadilloBrushing()) {
            Armadillo armadillo = EntityType.ARMADILLO.create(minecraft.level, EntitySpawnReason.COMMAND);
            if(armadillo == null) return;

            extractRenderState(graphics, mouseX, mouseY, armadillo, x - 5, y, scale);
        }
        graphics.fakeItem(Items.BRUSH.getDefaultInstance(), x - 1, y - 8);
    }

    @Override
    public int getColour() {
        return CommonColors.SOFT_RED;
    }

    @Override
    protected Component getTooltip() {
        if(lootTableId.isArmadilloBrushing()) {
            return Component.literal("Armadillo Brushing");
        }
        return super.getTooltip();
    }
}
