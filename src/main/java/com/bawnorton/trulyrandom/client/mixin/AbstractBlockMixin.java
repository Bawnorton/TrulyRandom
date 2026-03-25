package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.AbstractBlockAccessor;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {
    @ModifyReturnValue(method = "getSoundGroup", at = @At("RETURN"))
    private BlockSoundGroup useRandomisedBlockSound(BlockSoundGroup original) {
        UnaryMap<BlockState> originalToRandomMap = ((ModelShuffler.BlockStates) MinecraftClient.getInstance()
                .getBlockRenderManager()
                .getModels()).trulyrandom$getRedirectMap();
        BlockState defaultState;
        if((Object) this instanceof Block block) {
            defaultState = block.getDefaultState();
        } else {
            return original;
        }
        return ((AbstractBlockAccessor) originalToRandomMap.getOrDefault(defaultState, defaultState).getBlock()).trulyrandom$getSoundGroup();
    }
}
