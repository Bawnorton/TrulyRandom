package com.bawnorton.trulyrandom.random.module.state;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

public interface ModuleState {
    Codec<ModuleState> CODEC = Type.REGISTRY.byNameCodec()
            .dispatch("type", ModuleState::getType, Type::codec);

    StreamCodec<RegistryFriendlyByteBuf, ModuleState> STREAM_CODEC = ByteBufCodecs.registry(Type.REGISTRY.key())
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

    record Type<T extends ModuleState>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> packetCodec) {
        public static final ResourceKey<Registry<Type<?>>> KEY = ResourceKey.createRegistryKey(TrulyRandom.id("module_types"));

        public static Registry<Type<?>> REGISTRY;
    }
}
