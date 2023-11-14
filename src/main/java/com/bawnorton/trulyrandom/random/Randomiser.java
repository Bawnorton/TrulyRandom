package com.bawnorton.trulyrandom.random;

import net.minecraft.nbt.NbtCompound;

import java.util.Random;
import java.util.function.BiConsumer;

public class Randomiser {
    private Random sessionRandom;
    private Modules modules;
    private long seed;

    public Randomiser(long seed) {
        this.seed = seed;
        this.sessionRandom = new Random(seed);
        this.modules = new Modules();
    }

    public static Randomiser fromNbt(NbtCompound nbt) {
        Randomiser randomiser = new Randomiser(nbt.getLong("seed"));
        randomiser.modules = new Modules();
        randomiser.modules.readNbt(nbt.getCompound("modules"));
        return randomiser;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLong("seed", seed);
        NbtCompound modulesNbt = new NbtCompound();
        modules.writeNbt(modulesNbt);
        nbt.put("modules", modulesNbt);
        return nbt;
    }

    public long getSeed() {
        return seed;
    }

    public Modules getModules() {
        return modules;
    }

    public void setModules(Modules modules) {
        this.modules = modules;
    }

    public void newSessionRandom(long seed) {
        this.seed = seed;
        sessionRandom = new Random(seed);
    }

    public Random getSessionRandom() {
        return sessionRandom;
    }

    public void shouldShuffleModels(BiConsumer<Boolean, Boolean> consumer) {
        consumer.accept(modules.isEnabled(Module.ITEM_MODELS), modules.isEnabled(Module.BLOCK_MODELS));
    }
}
