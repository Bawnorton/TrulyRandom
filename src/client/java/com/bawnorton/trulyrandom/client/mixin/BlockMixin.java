package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.AbstractBlockAccessor;
import com.bawnorton.trulyrandom.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.collection.UnaryMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Map;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlockMixin {
    @Shadow
    public abstract BlockState getDefaultState();

    @ModifyReturnValue(method = "getSlipperiness", at = @At("RETURN"))
    private float useRandomisedSlipperiness(float original) {
        UnaryMap<BlockState> originalToRandomMap = ((ModelShuffler.BlockStates) MinecraftClient.getInstance()
                .getBlockRenderManager()
                .getModels()).trulyrandom$getRedirectMap();
        return ((AbstractBlockAccessor) originalToRandomMap.getOrDefault(getDefaultState(), getDefaultState()).getBlock()).trulyrandom$getSlipperiness();
    }
}
