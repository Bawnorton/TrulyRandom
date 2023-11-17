package com.bawnorton.trulyrandom.world;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.Modules;
import com.bawnorton.trulyrandom.random.Randomiser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class RandomiserSaveLoader extends PersistentState {
    private static final Type<RandomiserSaveLoader> type = new Type<>(
            RandomiserSaveLoader::new,
            RandomiserSaveLoader::fromNbt,
            null
    );
    private static boolean defaultSet = false;
    private static long defaultSeed;
    private static Modules defaultModules;
    private static Randomiser lastSetRandomiser;
    private Randomiser randomiser;


    public static void setDefaultRandomiser(long seed, Modules modules) {
        defaultSet = true;
        defaultSeed = seed;
        defaultModules = modules;
    }

    public static RandomiserSaveLoader fromNbt(NbtCompound nbt) {
        RandomiserSaveLoader randomiserSaveLoader = new RandomiserSaveLoader();
        randomiserSaveLoader.randomiser = Randomiser.fromNbt(nbt.getCompound("randomiser"));
        return randomiserSaveLoader;
    }

    public static RandomiserSaveLoader getServerState(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) throw new IllegalStateException("Tried to get randomiser state before world was loaded");
        PersistentStateManager manager = world.getPersistentStateManager();
        RandomiserSaveLoader state = manager.getOrCreate(type, TrulyRandom.MOD_ID);
        state.markDirty();
        lastSetRandomiser = state.getRandomiser();
        return state;
    }

    public static Randomiser fetchUnsafeRandomiser() {
        if (lastSetRandomiser != null) return lastSetRandomiser;
        if (!defaultSet) throw new IllegalStateException("Default randomiser not set");
        Randomiser defaultRandomiser = new Randomiser(defaultSeed);
        defaultRandomiser.setModules(defaultModules);
        return defaultRandomiser;
    }

    public Randomiser getRandomiser() {
        if (randomiser == null) {
            if (!defaultSet) throw new IllegalStateException("Default randomiser not set");
            randomiser = new Randomiser(defaultSeed);
            randomiser.setModules(defaultModules);
        }
        return randomiser;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("randomiser", randomiser.writeNbt(new NbtCompound()));
        return nbt;
    }
}
