package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.minecraft.registry.Registry;

public final class ModuleStateTypes {
    public static final ModuleState.Type<StandardModuleState> STANDARD = register("standard", new ModuleState.Type<>(StandardModuleState.CODEC, StandardModuleState.PACKET_CODEC));
    public static final ModuleState.Type<RecipeModuleState> RECIPE = register("recipe", new ModuleState.Type<>(RecipeModuleState.CODEC, RecipeModuleState.PACKET_CODEC));

    public static <T extends ModuleState> ModuleState.Type<T> register(String id, ModuleState.Type<T> moduleType) {
        return Registry.register(ModuleState.Type.REGISTRY, TrulyRandom.id(id), moduleType);
    }

    public static void init() {
        //no-op
    }
}
