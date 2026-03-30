package com.bawnorton.trulyrandom.mixin.registry;

import com.bawnorton.trulyrandom.random.module.state.ModuleState;
import com.bawnorton.trulyrandom.random.module.state.ModuleStateTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BuiltInRegistries.class)
abstract class BuiltInRegistriesMixin {
    @Shadow
    private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> name, BuiltInRegistries.RegistryBootstrap<T> loader) {
        throw new AssertionError();
    }

    static {
        ModuleState.Type.REGISTRY = registerSimple(ModuleState.Type.KEY, (_) -> ModuleStateTypes.STANDARD);
    }
}
