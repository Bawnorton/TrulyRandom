package com.bawnorton.trulyrandom.random.module;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;

public enum ModuleCategory {
    GENERAL(true, true),
    CLIENT(true, false),
    WORLD_GEN(false, true);

    private final boolean mutable;
    private final boolean serverSide;

    ModuleCategory(boolean mutable, boolean serverSide) {
        this.mutable = mutable;
        this.serverSide = serverSide;
    }

    public boolean isMutable() {
        return mutable;
    }

    public boolean isServerSide() {
        return serverSide;
    }

    public MutableComponent getDisplayName() {
        return Component.translatable("selectWorld.trulyrandom.category.%s".formatted(name().toLowerCase(Locale.ENGLISH)));
    }
}