package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.AbstractBlockAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock {
    protected BlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    public abstract BlockState getDefaultState();

    @ModifyReturnValue(method = "getSoundGroup", at = @At("RETURN"))
    private BlockSoundGroup useRandomisedBlockSound(BlockSoundGroup original) {
        Map<BlockState, BlockState> originalToRandomMap = ((ModelShuffler.BlockStates) MinecraftClient.getInstance()
                .getBlockRenderManager()
                .getModels()).trulyrandom$getOriginalRandomisedMap();
        return ((AbstractBlockAccessor) originalToRandomMap.getOrDefault(getDefaultState(), getDefaultState())
                .getBlock()).getSoundGroup();
    }
}
