package com.bawnorton.trulyrandom.random.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import java.util.Random;

public class StandardModuleState implements ModuleState {
    public static final MapCodec<StandardModuleState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", false).forGetter(StandardModuleState::isEnabled),
            Codec.BOOL.optionalFieldOf("visible", true).forGetter(StandardModuleState::isVisible),
            Codec.LONG.optionalFieldOf("seed", new Random().nextLong()).forGetter(StandardModuleState::getSeed)
    ).apply(instance, StandardModuleState::new));

    public static final PacketCodec<RegistryByteBuf, StandardModuleState> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, StandardModuleState::isEnabled,
            PacketCodecs.VAR_LONG, StandardModuleState::getSeed,
            StandardModuleState::new
    );

    private boolean enabled;
    private boolean visible;
    private long seed;

    public StandardModuleState(boolean enabled, boolean visible, long seed) {
        this.enabled = enabled;
        this.visible = visible;
        this.seed = seed;
    }

    private StandardModuleState(boolean enabled, long seed) {
        this(enabled, true, seed);
    }

    public StandardModuleState() {
        this(false, true, new Random().nextLong());
    }

    public Type<?> getType() {
        return ModuleStateTypes.STANDARD;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void show() {
        visible = true;
    }

    public void hide() {
        visible = false;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void randomSeed() {
        seed = new Random().nextLong();
    }

    public StandardModuleState copy() {
        return new StandardModuleState(enabled, visible, seed);
    }
}
