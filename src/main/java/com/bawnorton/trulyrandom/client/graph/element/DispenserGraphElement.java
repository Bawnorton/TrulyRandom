package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DispenserGraphElement extends IdBasedGraphElement {
    public DispenserGraphElement(LootTableIdentifier lootTableId) {
        super(lootTableId);
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int mouseX, int mouseY, int x, int y, float scale) {
        context.drawItemWithoutEntity(Items.DISPENSER.getDefaultInstance(), x - 8, y - 8);
    }

    @Override
    protected Text getTooltip() {
        String[] segments = lootTableId.getSegments();
        String name = segments[segments.length - 1];
        name = Arrays.stream(name.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(name.endsWith("Dispenser")) {
            return Text.of(name);
        }
        return Text.of("%s Dispenser".formatted(name));
    }
}
