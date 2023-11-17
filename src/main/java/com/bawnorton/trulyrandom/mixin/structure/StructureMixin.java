package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.Module;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Structure.class)
public abstract class StructureMixin {
    @ModifyReturnValue(method = "isBiomeValid", at = @At("RETURN"))
    private static boolean setBiomeValidIfStructureRandomiserEnabled(boolean original) {
        if(original) return true;
        return TrulyRandom.getUnsafeRandomiser().getModules().isEnabled(Module.STRUCTURES);
    }
}
