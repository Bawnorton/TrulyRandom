package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.AbstractBlockAccessor;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@MixinEnvironment("client")
@Mixin(Block.class)
abstract class BlockMixin extends AbstractBlockMixin {

    @Shadow
    public abstract BlockState defaultBlockState();

    @ModifyReturnValue(method = "getFriction", at = @At("RETURN"))
    private float useRandomisedSlipperiness(float original) {
        UnaryMap<BlockState> originalToRandomMap = ((ModelShuffler.BlockStates) Minecraft.getInstance()
                .getModelManager()
                .getBlockStateModelSet()).trulyrandom$getRedirectMap();
        return ((AbstractBlockAccessor) originalToRandomMap.getOrDefault(defaultBlockState(), defaultBlockState()).getBlock()).trulyrandom$friction();
    }
}
