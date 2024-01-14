package com.bawnorton.trulyrandom.world;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RandomiserSaveLoader extends PersistentState {
    private static boolean defaultSet = false;
    private static Modules defaultModules;
    private static ServerRandomiser lastSetRandomiser;

    private ServerRandomiser serverRandomiser;
    private Map<UUID, Modules> clientRandomisers;

    public static void setDefaultRandomiser(Modules modules) {
        defaultSet = true;
        defaultModules = modules;
    }

    public static RandomiserSaveLoader fromNbt(NbtCompound nbt) {
        RandomiserSaveLoader state = new RandomiserSaveLoader();
        state.serverRandomiser = ServerRandomiser.fromNbt(nbt.getCompound("randomiser"));
        state.clientRandomisers = new HashMap<>();
        NbtCompound clientRandomisers = nbt.getCompound("client_randomisers");
        clientRandomisers.getKeys().forEach(uuid -> state.getClientRandomisers().put(UUID.fromString(uuid), Modules.fromNbt(clientRandomisers.getCompound(uuid))));
        return state;
    }

    public static RandomiserSaveLoader getServerState(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        if (world == null) throw new IllegalStateException("Tried to get randomiser state before world was loaded");
        PersistentStateManager manager = world.getPersistentStateManager();
        RandomiserSaveLoader state = manager.getOrCreate(RandomiserSaveLoader::fromNbt, RandomiserSaveLoader::new, TrulyRandom.MOD_ID);
        state.markDirty();
        lastSetRandomiser = state.getServerRandomiser();
        return state;
    }

    public static ServerRandomiser fetchUnsafeRandomiser() {
        if (lastSetRandomiser != null) return lastSetRandomiser;

        ServerRandomiser def = ServerRandomiser.DEFAULT;
        setDefaultRandomiser(def.getModules());
        return def;
    }

    public ServerRandomiser getServerRandomiser() {
        if (serverRandomiser == null) {
            if (!defaultSet) throw new IllegalStateException("Default randomiser not set");

            serverRandomiser = new ServerRandomiser(new Modules());
            serverRandomiser.setModules(defaultModules);
        }
        return serverRandomiser;
    }

    private Map<UUID, Modules> getClientRandomisers() {
        if (clientRandomisers == null) clientRandomisers = new HashMap<>();
        return clientRandomisers;
    }

    public Randomiser getClientRandomiser(UUID uuid) {
        Modules modules = getClientRandomisers().computeIfAbsent(uuid, k -> new Modules());

        return new ServerRandomiser(modules);
    }

    public void setClientRandomiser(UUID uuid, Modules modules) {
        getClientRandomisers().put(uuid, modules);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("randomiser", getServerRandomiser().writeNbt(new NbtCompound()));
        NbtCompound clientRandomisers = new NbtCompound();
        getClientRandomisers().forEach((uuid, modules) -> clientRandomisers.put(uuid.toString(), modules.writeNbt(new NbtCompound())));
        nbt.put("client_randomisers", clientRandomisers);
        return nbt;
    }
}
