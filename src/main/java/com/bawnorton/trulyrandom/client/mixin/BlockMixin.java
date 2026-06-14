package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.AbstractBlockAccessor;
import com.bawnorton.trulyrandom.client.mixin.accessor.ModelManagerAccessor;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockStateModelSet;
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
        ModelManagerAccessor modelManagerAccessor = (ModelManagerAccessor) Minecraft.getInstance().getModelManager();
        BlockStateModelSet stateModelSet = modelManagerAccessor.trulyrandom$blockStateModelSet();
        Block block;
        if (stateModelSet instanceof ModelShuffler.BlockStates shuffler) {
            block = shuffler.trulyrandom$getRedirectMap().getOrDefault(defaultBlockState(), defaultBlockState()).getBlock();
        } else {
            block = defaultBlockState().getBlock();
        }
        return ((AbstractBlockAccessor) block).trulyrandom$friction();
    }
}
