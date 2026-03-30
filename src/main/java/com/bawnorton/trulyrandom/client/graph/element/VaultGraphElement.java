package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.screen.render.BlockStateElementRenderState;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class VaultGraphElement extends IdBasedGraphElement {
    public VaultGraphElement(LootTableIdentifier tableId) {
        super(tableId);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        VaultBlock vaultBlock = (VaultBlock) Blocks.VAULT;
        BlockState state = vaultBlock.defaultBlockState();
        if(lootTableId.isOminous()) {
            state = state.setValue(VaultBlock.OMINOUS, true);
        }
        graphics.guiRenderState.addPicturesInPictureState(new BlockStateElementRenderState(
                state, x, y, scale, 225, graphics.scissorStack.peek()
        ));
    }

    @Override
    public int getColour() {
        return CommonColors.COSMOS_PINK;
    }

    @Override
    protected Component getTooltip() {
        String[] segments = lootTableId.getSegments();
        String name = segments[segments.length - 1];
        name = Arrays.stream(name.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(name.endsWith("Vault")) {
            return Component.literal(name);
        }
        return Component.literal("%s Vault".formatted(name));
    }
}
