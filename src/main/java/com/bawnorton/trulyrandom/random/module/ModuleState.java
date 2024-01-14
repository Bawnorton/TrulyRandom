package com.bawnorton.trulyrandom.random.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

import java.util.Random;

public final class ModuleState {
    public static final Codec<ModuleState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("enabled", false).forGetter(ModuleState::isEnabled),
            Codec.BOOL.optionalFieldOf("visible", true).forGetter(ModuleState::isVisible),
            Codec.LONG.optionalFieldOf("seed", new Random().nextLong()).forGetter(ModuleState::getSeed)
    ).apply(instance, ModuleState::new));

    private boolean enabled;
    private boolean visible;
    private long seed;

    public ModuleState(boolean enabled, boolean visible, long seed) {
        this.enabled = enabled;
        this.visible = visible;
        this.seed = seed;
    }

    public ModuleState() {
        this(false, true, new Random().nextLong());
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

    public ModuleState copy() {
        return new ModuleState(enabled, visible, seed);
    }

    public void write(PacketByteBuf buf) {
        buf.writeBoolean(enabled);
        buf.writeLong(seed);
    }

    public void read(PacketByteBuf buf) {
        enabled = buf.readBoolean();
        seed = buf.readLong();
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("enabled", enabled);
        nbt.putLong("seed", seed);
    }

    public void readNbt(NbtCompound nbt) {
        enabled = nbt.getBoolean("enabled");
        seed = nbt.getLong("seed");
    }
}
