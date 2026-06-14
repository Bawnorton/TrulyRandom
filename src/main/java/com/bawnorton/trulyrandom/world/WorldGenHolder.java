package com.bawnorton.trulyrandom.world;

import com.bawnorton.trulyrandom.random.feature.FeatureReplacementRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.structure.StructureReplacementRandomiser;
import org.jetbrains.annotations.NotNull;

public record WorldGenHolder(Modules modules, StructureReplacementRandomiser structureReplacementRandomiser, FeatureReplacementRandomiser featureReplacementRandomiser) {
    public WorldGenHolder(@NotNull Modules modules) {
        this(modules, new StructureReplacementRandomiser(modules.getSeed(Module.STRUCTURES)), new FeatureReplacementRandomiser(modules.getSeed(Module.FEATURES)));
    }
}
