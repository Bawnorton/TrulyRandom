package com.bawnorton.trulyrandom.random;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;

public enum Module {
    LOOT_TABLES(true, true),
    RECIPES(true, true),
    BLOCK_MODELS(true, true),
    ITEM_MODELS(true, true),
    STRUCTURES(true, false);

    public static final Codec<Module> CODEC = Codec.STRING.xmap(Module::valueOf, Module::name);

    private final boolean implemented;
    private final boolean mutable;

    private boolean enabled;

    Module(boolean implemented, boolean mutable) {
        this.implemented = implemented;
        this.mutable = mutable;
    }

    public boolean isImplemented() {
        return implemented;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isMutable() {
        return mutable;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean(name(), enabled);
    }

    public void readNbt(NbtCompound nbt) {
        enabled = nbt.getBoolean(name());
    }
}
