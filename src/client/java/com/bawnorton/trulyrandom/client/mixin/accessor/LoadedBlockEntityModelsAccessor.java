package com.bawnorton.trulyrandom.client.mixin.accessor;

import net.minecraft.block.Block;
import net.minecraft.client.render.block.entity.LoadedBlockEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(LoadedBlockEntityModels.class)
public interface LoadedBlockEntityModelsAccessor {
    @Accessor
    Map<Block, SpecialModelRenderer<?>> getRenderers();
}
