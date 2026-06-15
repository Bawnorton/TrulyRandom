package com.bawnorton.trulyrandom.random.module.state;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.minecraft.core.Registry;

public final class ModuleStateTypes {
    public static final ModuleState.Type<StandardModuleState> STANDARD = register("standard", new ModuleState.Type<>(StandardModuleState.CODEC, StandardModuleState.STREAM_CODEC));
    public static final ModuleState.Type<RecipeModuleState> RECIPE = register("recipe", new ModuleState.Type<>(RecipeModuleState.CODEC, RecipeModuleState.STREAM_CODEC));
    public static final ModuleState.Type<LootModuleState> LOOT = register("loot", new ModuleState.Type<>(LootModuleState.CODEC, LootModuleState.STREAM_CODEC));
    public static final ModuleState.Type<BlockModelModuleState> BLOCK_MODEL = register("block_model", new ModuleState.Type<>(BlockModelModuleState.CODEC, BlockModelModuleState.STREAM_CODEC));
    public static final ModuleState.Type<ItemModelModuleState> ITEM_MODEL = register("item_model", new ModuleState.Type<>(ItemModelModuleState.CODEC, ItemModelModuleState.STREAM_CODEC));
    public static final ModuleState.Type<StructureModuleState> STRUCTURE = register("structure", new ModuleState.Type<>(StructureModuleState.CODEC, StructureModuleState.STREAM_CODEC));
    public static final ModuleState.Type<BlockPaletteModuleState> BLOCK_PALETTE = register("block_palette", new ModuleState.Type<>(BlockPaletteModuleState.CODEC, BlockPaletteModuleState.STREAM_CODEC));

    public static <T extends ModuleState> ModuleState.Type<T> register(String id, ModuleState.Type<T> moduleType) {
        return Registry.register(ModuleState.Type.REGISTRY, TrulyRandom.id(id), moduleType);
    }

    public static void init() {
        //no-op
    }
}
