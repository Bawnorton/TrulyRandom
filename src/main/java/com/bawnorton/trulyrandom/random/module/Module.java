package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.random.module.state.*;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;

public enum Module {
    BLOCK_MODELS(ModuleCategory.CLIENT, true, BlockModelModuleState::new),
    ITEM_MODELS(ModuleCategory.CLIENT, true, ItemModelModuleState::new),
    LOOT_TABLES(ModuleCategory.GENERAL, true, LootModuleState::new),
    RECIPES(ModuleCategory.GENERAL, true, RecipeModuleState::new),
    TRADES(ModuleCategory.GENERAL, true, StandardModuleState::new),
    STRUCTURES(ModuleCategory.WORLD_GEN, true, StructureModuleState::new),
    FEATURES(ModuleCategory.WORLD_GEN, true, StandardModuleState::new),
    BLOCK_PALETTE(ModuleCategory.WORLD_GEN, true, BlockPaletteModuleState::new);

    public static final Codec<Module> CODEC = Codec.STRING.xmap(Module::valueOf, Module::name);
    public static final StreamCodec<ByteBuf, Module> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(Module::valueOf, Module::name);

    private final boolean implemented;
    private final Supplier<ModuleState> moduleStateSupplier;
    private final ModuleCategory category;

    Module(ModuleCategory category, boolean implemented, Supplier<ModuleState> moduleStateSupplier) {
        this.implemented = implemented;
        this.moduleStateSupplier = moduleStateSupplier;
        this.category = category;
    }

    public ModuleState newModuleState() {
        return moduleStateSupplier.get();
    }

    public boolean isImplemented() {
        return implemented;
    }

    public boolean isMutable() {
        return category.isMutable();
    }

    public boolean isServerSide() {
        return category.isServerSide();
    }

    public ModuleCategory getCategory() {
        return category;
    }
}
