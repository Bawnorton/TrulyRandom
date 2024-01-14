package com.bawnorton.trulyrandom.random.module;

import com.mojang.serialization.Codec;

public enum Module {
    BLOCK_MODELS(true, false, true),
    ITEM_MODELS(true, false, true),
    LOOT_TABLES(true, true, true),
    RECIPES(true, true, true),
    STRUCTURES(true, true, false);

    public static final Codec<Module> CODEC = Codec.STRING.xmap(Module::valueOf, Module::name);

    private final boolean implemented;
    private final boolean mutable;
    private final boolean serverSide;

    Module(boolean implemented, boolean serverSide, boolean mutable) {
        this.implemented = implemented;
        this.mutable = mutable;
        this.serverSide = serverSide;
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
