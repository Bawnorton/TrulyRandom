package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.state.StructureModuleState;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.bawnorton.trulyrandom.world.WorldGenHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Structure.class)
public abstract class StructureMixin {
    @ModifyReturnValue(
            method = "isValidBiome",
            at = @At("RETURN")
    )
    private static boolean setBiomeValidIfStructureRandomiserEnabled(boolean original) {
        if (original) return true;

        WorldGenHolder worldGenHolder = RandomiserSaveLoader.getWorldGenHolder();
        Modules modules = worldGenHolder.modules();
        StructureModuleState state = modules.getState(Module.STRUCTURES, StructureModuleState.class);
        return state.isEnabled() && !state.replaceStructuresInstead();
    }
}
