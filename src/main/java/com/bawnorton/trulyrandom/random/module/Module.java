package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.random.module.state.ModuleState;
import com.bawnorton.trulyrandom.random.module.state.RecipeModuleState;
import com.bawnorton.trulyrandom.random.module.state.StandardModuleState;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

public enum Module {
    BLOCK_MODELS(true, false, true),
    ITEM_MODELS(true, false, true),
    LOOT_TABLES(true, true, true),
    RECIPES(true, true, true),
    TRADES(true, true, true),
    STRUCTURES(true, true, false),
    FEATURES(false, true, false);

    public static final Codec<Module> CODEC = Codec.STRING.xmap(Module::valueOf, Module::name);
    public static final StreamCodec<ByteBuf, Module> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(Module::valueOf, Module::name);

    private final boolean implemented;
    private final boolean mutable;
    private final boolean serverSide;

    Module(boolean implemented, boolean serverSide, boolean mutable) {
        this.implemented = implemented;
        this.mutable = mutable;
        this.serverSide = serverSide;
    }

    public ModuleState newModuleState() {
        return switch (this) {
            case RECIPES -> new RecipeModuleState();
            default -> new StandardModuleState();
        };
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
