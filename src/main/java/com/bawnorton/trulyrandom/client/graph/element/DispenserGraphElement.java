package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DispenserGraphElement extends IdBasedGraphElement {
    public DispenserGraphElement(LootTableIdentifier lootTableId) {
        super(lootTableId);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        graphics.fakeItem(Items.DISPENSER.getDefaultInstance(), x - 8, y - 8);
    }

    @Override
    protected Component getTooltip() {
        String[] segments = lootTableId.getSegments();
        String name = segments[segments.length - 1];
        name = Arrays.stream(name.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(name.endsWith("Dispenser")) {
            return Component.literal(name);
        }
        return Component.literal("%s Dispenser".formatted(name));
    }
}
