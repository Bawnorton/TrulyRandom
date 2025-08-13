package com.bawnorton.trulyrandom.world;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentStateType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RandomiserSaveLoader extends PersistentState {
    private static ServerRandomiser lastSetRandomiser;
    private static Modules worldGenModules;

    public static final PersistentStateType<RandomiserSaveLoader> TYPE = new PersistentStateType<>(
            TrulyRandom.MOD_ID,
            RandomiserSaveLoader::new,
            RandomiserSaveLoader::codec,
            null
    );

    private ServerRandomiser serverRandomiser;
    private Map<UUID, Modules> clientRandomisers;

    private RandomiserSaveLoader(Context context) {
        this.serverRandomiser = new ServerRandomiser(Objects.requireNonNullElseGet(worldGenModules, Modules::new), context.getWorldOrThrow().getServer());
        this.clientRandomisers = new HashMap<>();
        markDirty();
    }

    private RandomiserSaveLoader(ServerRandomiser serverRandomiser, Map<UUID, Modules> clientRandomisers) {
        this.serverRandomiser = serverRandomiser;
        this.clientRandomisers = clientRandomisers;
        markDirty();
    }

    public static Codec<RandomiserSaveLoader> codec(Context context) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ServerRandomiser.codec(context).fieldOf("randomiser").forGetter(RandomiserSaveLoader::getServerRandomiser),
                Codec.unboundedMap(Uuids.CODEC, Modules.CODEC).fieldOf("client_randomisers").forGetter(RandomiserSaveLoader::getClientRandomisers)
        ).apply(instance, RandomiserSaveLoader::new));
    }

    public static RandomiserSaveLoader getServerState(MinecraftServer server) {
        ServerWorld world = server.getOverworld();
        if (world == null) throw new IllegalStateException("Tried to get randomiser state before world was loaded");

        PersistentStateManager manager = world.getPersistentStateManager();
        RandomiserSaveLoader state = manager.getOrCreate(TYPE);
        state.markDirty();
        if(state.getServerRandomiser() == null) {
            state.serverRandomiser = new ServerRandomiser(Objects.requireNonNullElseGet(worldGenModules, Modules::new), server);
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

    public Randomiser getClientRandomiser(UUID uuid, MinecraftServer server) {
        Modules modules = getClientRandomisers().computeIfAbsent(uuid, k -> new Modules());

        return new ServerRandomiser(modules, server);
    }

    public void setClientRandomiser(UUID uuid, Modules modules) {
        getClientRandomisers().put(uuid, modules);
    }
}
