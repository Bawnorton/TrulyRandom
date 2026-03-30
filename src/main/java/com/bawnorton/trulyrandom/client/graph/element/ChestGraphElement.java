package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.mixin.accessor.BlockModelRenderStateAccessor;
import com.bawnorton.trulyrandom.client.mixin.accessor.ChestSpecialRendererAccessor;
import com.bawnorton.trulyrandom.client.screen.render.BlockStateElementRenderState;
import com.bawnorton.trulyrandom.client.screen.render.BlockStateGuiRenderer;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.joml.Matrix3x2fStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChestGraphElement extends IdBasedGraphElement {
    private final ChestRenderState chestRenderState;
    private final BlockModelRenderState blockModelRenderState;
    private int counter = 0;
    private int offset = 0;
    private final List<Item> chestContent;

    public ChestGraphElement(LootTableIdentifier lootTableId, List<Item> chestContent) {
        super(lootTableId);
        this.chestContent = chestContent;
        this.chestRenderState = new ChestRenderState();
        this.blockModelRenderState = new BlockModelRenderState();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        counter++;
        if(counter >= minecraft.getFps()) {
            counter = 0;
            offset++;
            if(offset >= chestContent.size()) {
                offset = 0;
            }
        }
        BlockState state = Blocks.CHEST.defaultBlockState();
        BlockModel blockModel = minecraft.getModelManager().getBlockModelSet().get(state);
        blockModel.update(blockModelRenderState, state, BlockStateGuiRenderer.BLOCK_DISPLAY_CONTEXT, 42);
        SpecialModelRenderer<?> specialModelRenderer = ((BlockModelRenderStateAccessor) blockModelRenderState).trulyrandom$specialRenderer();
        ((ChestSpecialRendererAccessor) specialModelRenderer).trulyrandom$openness(0.7f);
        graphics.guiRenderState.addPicturesInPictureState(new BlockStateElementRenderState(
                state, x, y + 2, scale, 225, graphics.scissorStack.peek()
        ));
        Matrix3x2fStack matrices = graphics.pose();
        matrices.pushMatrix();
        matrices.scale(0.5F, 0.5F);

        x = (int) (x / 0.5);
        y = (int) (y / 0.5);
        x -= 8;
        y -= 9;

        graphics.fakeItem(chestContent.get(offset).getDefaultInstance(), x, y);
        matrices.popMatrix();
    }

    @Override
    protected int getColour() {
        return CommonColors.SOFT_YELLOW;
    }

    @Override
    protected Component getTooltip() {
        String[] segments = lootTableId.getSegments();
        String name = segments[segments.length - 1];
        name = Arrays.stream(name.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(name.endsWith("Chest")) {
            return Component.literal(name);
        }
        return Component.literal("%s Chest".formatted(name));
    }
}
