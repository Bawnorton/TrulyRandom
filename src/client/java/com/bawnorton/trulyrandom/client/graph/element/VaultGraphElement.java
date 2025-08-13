package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.screen.render.BlockStateElementRenderState;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VaultBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VaultGraphElement extends IdBasedGraphElement {
    public VaultGraphElement(LootTableIdentifier tableId) {
        super(tableId);
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int mouseX, int mouseY, int x, int y, float scale) {
        VaultBlock vaultBlock = (VaultBlock) Blocks.VAULT;
        BlockState state = vaultBlock.getDefaultState();
        if(lootTableId.isOminous()) {
            state = state.with(VaultBlock.OMINOUS, true);
        }
        context.state.addSpecialElement(new BlockStateElementRenderState(
                state, x, y, scale, 225, context.scissorStack.peekLast()
        ));
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
