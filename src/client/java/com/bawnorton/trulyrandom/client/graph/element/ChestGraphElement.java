package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.mixin.accessor.ChestModelRendererAccessor;
import com.bawnorton.trulyrandom.client.mixin.accessor.LoadedBlockEntityModelsAccessor;
import com.bawnorton.trulyrandom.client.screen.BlockStateGuiRenderer;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.block.entity.LoadedBlockEntityModels;
import net.minecraft.client.render.item.model.special.ChestModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChestGraphElement extends IdBasedGraphElement implements BlockStateGuiRenderer {
    private int counter = 0;
    private int offset = 0;
    private final List<Item> chestContent;

    public ChestGraphElement(LootTableIdentifier lootTableId, List<Item> chestContent) {
        super(lootTableId);
        this.chestContent = chestContent;
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int mouseX, int mouseY, int x, int y, float scale) {
        counter++;
        if(counter >= client.getCurrentFps()) {
            counter = 0;
            offset++;
            if(offset >= chestContent.size()) {
                offset = 0;
            }
        }
        ChestBlock chest = (ChestBlock) Blocks.CHEST;
        BlockState state = chest.getDefaultState();
        LoadedBlockEntityModels models = client.getBakedModelManager().getBlockEntityModelsSupplier().get();
        Map<Block, SpecialModelRenderer<?>> renderers = ((LoadedBlockEntityModelsAccessor) models).getRenderers();
        ChestModelRenderer modelRenderer = (ChestModelRenderer) renderers.get(chest);
        ((ChestModelRendererAccessor) modelRenderer).setOpenness(0.7f);
        render(context, client, x, y + 2, 45, 0.8f, state);
        ((ChestModelRendererAccessor) modelRenderer).setOpenness(0);
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 300);
        context.getMatrices().scale(0.5F, 0.5F, 1);

        x = (int) (x / 0.5);
        y = (int) (y / 0.5);
        x -= 8;
        y -= 9;

        context.drawItemWithoutEntity(chestContent.get(offset).getDefaultStack(), x, y);
        context.getMatrices().pop();
    }

    @Override
    protected Text getTooltip() {
        String[] segments = lootTableId.getSegments();
        String name = segments[segments.length - 1];
        name = Arrays.stream(name.split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(name.endsWith("Chest")) {
            return Text.of(name);
        }
        return Text.of("%s Chest".formatted(name));
    }
}
