package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import java.util.Random;

public interface ModuleState {
    Codec<ModuleState> CODEC = Type.REGISTRY.getCodec()
            .dispatch("type", ModuleState::getType, Type::codec);

    PacketCodec<RegistryByteBuf, ModuleState> PACKET_CODEC = PacketCodecs.registryCodec(Type.REGISTRY.getCodec())
            .dispatch(ModuleState::getType, Type::packetCodec);

    Type<?> getType();

    boolean isEnabled();

    void enable();

    void disable();

    boolean isVisible();

    void show();

    void hide();

    long getSeed();

    void setSeed(long seed);

    void randomSeed();

    ModuleState copy();

    record Type<T extends ModuleState>(MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
        public static final Registry<Type<?>> REGISTRY = new SimpleRegistry<>(
                RegistryKey.ofRegistry(TrulyRandom.id("module_types")), Lifecycle.stable());
    }
}
