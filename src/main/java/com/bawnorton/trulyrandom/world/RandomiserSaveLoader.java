package com.bawnorton.trulyrandom.world;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RandomiserSaveLoader extends PersistentState {
    private static ServerRandomiser lastSetRandomiser;
    private static Modules worldGenModules;

    public static final PersistentState.Type<RandomiserSaveLoader> TYPE = new Type<>(
            RandomiserSaveLoader::new,
            RandomiserSaveLoader::fromNbt,
            null
    );

    private ServerRandomiser serverRandomiser;
    private Map<UUID, Modules> clientRandomisers;

    public static RandomiserSaveLoader fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        RandomiserSaveLoader state = new RandomiserSaveLoader();
        state.serverRandomiser = ServerRandomiser.fromNbt(nbt.getCompound("randomiser"), registryLookup);
        state.clientRandomisers = new HashMap<>();
        NbtCompound clientRandomisers = nbt.getCompound("client_randomisers");
        clientRandomisers.getKeys().forEach(uuid -> state.getClientRandomisers().put(UUID.fromString(uuid), Modules.fromNbt(clientRandomisers.getCompound(uuid))));
        return state;
    }

    public static RandomiserSaveLoader getServerState(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        if (world == null) throw new IllegalStateException("Tried to get randomiser state before world was loaded");

        PersistentStateManager manager = world.getPersistentStateManager();
        RandomiserSaveLoader state = manager.getOrCreate(TYPE, TrulyRandom.MOD_ID);
        state.markDirty();
        if(state.getServerRandomiser() == null) {
            state.serverRandomiser = new ServerRandomiser(Objects.requireNonNullElseGet(worldGenModules, Modules::new));
        }
        lastSetRandomiser = state.getServerRandomiser();
        return state;
    }

    public static ServerRandomiser getLastSetRandomiser() {
        if (lastSetRandomiser != null) return lastSetRandomiser;

        throw new IllegalStateException("Tried to get randomiser state before world was loaded");
    }

    public static boolean isLastSetRandomiserPresent() {
        return lastSetRandomiser != null;
    }

    public static void setWorldGenModules(Modules modules) {
        worldGenModules = modules;
    }

    public static Modules getWorldGenModules() {
        if(worldGenModules == null) return lastSetRandomiser.getModules();

        return worldGenModules;
    }

    public ServerRandomiser getServerRandomiser() {
        return serverRandomiser;
    }

    private Map<UUID, Modules> getClientRandomisers() {
        if (clientRandomisers == null) {
            clientRandomisers = new HashMap<>();
        }
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
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.put("randomiser", getServerRandomiser().writeNbt(new NbtCompound()));
        NbtCompound clientRandomisers = new NbtCompound();
        getClientRandomisers().forEach((uuid, modules) -> clientRandomisers.put(uuid.toString(), modules.writeNbt(new NbtCompound())));
        nbt.put("client_randomisers", clientRandomisers);
        return nbt;
    }
}
