package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.random.module.state.*;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;

public enum Module {
    BLOCK_MODELS(true, false, true, BlockModelModuleState::new),
    ITEM_MODELS(true, false, true, StandardModuleState::new),
    LOOT_TABLES(true, true, true, LootModuleState::new),
    RECIPES(true, true, true, RecipeModuleState::new),
    TRADES(true, true, true, StandardModuleState::new),
    STRUCTURES(true, true, false, StructureModuleState::new),
    FEATURES(true, true, false, StandardModuleState::new);

    public static final Codec<Module> CODEC = Codec.STRING.xmap(Module::valueOf, Module::name);
    public static final StreamCodec<ByteBuf, Module> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(Module::valueOf, Module::name);

    private final boolean implemented;
    private final boolean mutable;
    private final boolean serverSide;
    private final Supplier<ModuleState> moduleStateSupplier;

    Module(boolean implemented, boolean serverSide, boolean mutable, Supplier<ModuleState> moduleStateSupplier) {
        this.implemented = implemented;
        this.mutable = mutable;
        this.serverSide = serverSide;
        this.moduleStateSupplier = moduleStateSupplier;
    }

    public ModuleState newModuleState() {
        return moduleStateSupplier.get();
    }

    public boolean isImplemented() {
        return implemented;
    }

    public boolean isMutable() {
        return mutable;
    }

    public boolean isServerSide() {
        return serverSide;
    }
}
