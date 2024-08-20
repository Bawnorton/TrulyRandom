package com.bawnorton.trulyrandom.random.module;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public enum Module {
    BLOCK_MODELS(true, false, true),
    ITEM_MODELS(true, false, true),
    LOOT_TABLES(true, true, true),
    RECIPES(true, true, true),
    TRADES(true, true, true),
    STRUCTURES(true, true, false),
    FEATURES(false, true, false);

    public static final Codec<Module> CODEC = Codec.STRING.xmap(Module::valueOf, Module::name);
    public static final PacketCodec<ByteBuf, Module> PACKET_CODEC = PacketCodecs.STRING.xmap(Module::valueOf, Module::name);

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
