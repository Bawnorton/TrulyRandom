package com.bawnorton.trulyrandom.client.loot.graph.element;

import com.bawnorton.trulyrandom.client.screen.BlockStateGuiRenderer;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VaultBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VaultGraphElement extends IdBasedGraphElement implements BlockStateGuiRenderer {
    private final List<Item> vaultContent;

    public VaultGraphElement(LootTableIdentifier tableId, List<Item> items) {
        super(tableId);
        vaultContent = items;
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int x, int y, float scale) {
        VaultBlock vaultBlock = (VaultBlock) Blocks.VAULT;
        BlockState state = vaultBlock.getDefaultState();
        if(lootTableId.isOminous()) {
            state = state.with(VaultBlock.OMINOUS, true);
        }
        context.getMatrices().push();
        render(context, client, x, y, 225, scale, state);
        context.getMatrices().pop();
    }

    @Override
    protected Text getTooltip() {
        String[] segments = lootTableId.getSegments();
        String name = segments[segments.length - 1];
        name = Arrays.stream(name.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(name.endsWith("Vault")) {
            return Text.of(name);
        }
        return Text.of("%s Vault".formatted(name));
    }
}
