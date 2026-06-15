package com.bawnorton.trulyrandom.mixin.feature;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.bawnorton.trulyrandom.world.WorldGenHolder;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BiomeFilter.class)
abstract class BiomeFilterMixin {
    @WrapMethod(
            method = "shouldPlace"
    )
    private boolean alwaysYesIfRandomised(PlacementContext context, RandomSource random, BlockPos origin, Operation<Boolean> original) {
        WorldGenHolder worldGenHolder = RandomiserSaveLoader.getWorldGenHolder();
        Modules modules = worldGenHolder.modules();
        if (modules.isEnabled(Module.FEATURES)) return true;

        return original.call(context, random, origin);
    }
}
