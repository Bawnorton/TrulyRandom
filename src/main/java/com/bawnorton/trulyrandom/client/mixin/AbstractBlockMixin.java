package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.AbstractBlockAccessor;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@MixinEnvironment("client")
@Mixin(BlockBehaviour.class)
abstract class AbstractBlockMixin {
    @ModifyReturnValue(method = "getSoundType", at = @At("RETURN"))
    private SoundType useRandomisedBlockSound(SoundType original) {
        UnaryMap<BlockState> originalToRandomMap = ((ModelShuffler.BlockStates) Minecraft.getInstance()
                .getModelManager()
                .getBlockStateModelSet()).trulyrandom$getRedirectMap();
        BlockState defaultState;
        if((Object) this instanceof Block block) {
            defaultState = block.defaultBlockState();
        } else {
            return original;
        }
        return ((AbstractBlockAccessor) originalToRandomMap.getOrDefault(defaultState, defaultState).getBlock()).trulyrandom$soundType();
    }
}
