package com.bawnorton.trulyrandom.world;

import com.bawnorton.trulyrandom.random.block_palette.BlockPaletteRandomiser;
import com.bawnorton.trulyrandom.random.feature.FeatureRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.state.BlockPaletteModuleState;
import com.bawnorton.trulyrandom.random.structure.StructureRandomiser;
import org.jetbrains.annotations.NotNull;

public record WorldGenHolder(
        Modules modules,
        StructureRandomiser structureRandomiser,
        FeatureRandomiser featureRandomiser,
        BlockPaletteRandomiser blockPaletteRandomiser
) {
    public WorldGenHolder(@NotNull Modules modules) {
        this(modules,
                new StructureRandomiser(modules.getSeed(Module.STRUCTURES)),
                new FeatureRandomiser(modules.getSeed(Module.FEATURES)),
                new BlockPaletteRandomiser(
                        modules.getSeed(Module.BLOCK_PALETTE),
                        modules.getState(Module.BLOCK_PALETTE, BlockPaletteModuleState.class).isIgnoreStateProperties(),
                        modules.getState(Module.BLOCK_PALETTE, BlockPaletteModuleState.class).isIgnoreCollisionShape(),
                        modules.getState(Module.BLOCK_PALETTE, BlockPaletteModuleState.class).isIgnoreOcclusionShape(),
                        modules.getState(Module.BLOCK_PALETTE, BlockPaletteModuleState.class).isKeepStoneAsStone()
                )
        );
    }
}
