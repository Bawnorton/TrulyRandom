package com.bawnorton.trulyrandom.mixin.block_palette;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.bawnorton.trulyrandom.world.WorldGenHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProtoChunk.class)
abstract class ProtoChunkMixin {
    @WrapOperation(
            method = "setBlockState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState useRandomisedBlockState(LevelChunkSection instance, int sectionX, int sectionY, int sectionZ, BlockState state, Operation<BlockState> original) {
        WorldGenHolder worldGenHolder = RandomiserSaveLoader.getWorldGenHolder();
        Modules modules = worldGenHolder.modules();
        if (modules.isEnabled(Module.BLOCK_PALETTE)) {
            state = worldGenHolder.blockPaletteRandomiser().maybeReplaceBlock(state);
        }

        return original.call(instance, sectionX, sectionY, sectionZ, state);
    }
}
