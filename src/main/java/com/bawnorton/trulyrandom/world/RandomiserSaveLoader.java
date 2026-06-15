package com.bawnorton.trulyrandom.world;

import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.UUID;

public class RandomiserSaveLoader extends SavedData {
    private static ServerRandomiser lastSetRandomiser;
    private static WorldGenHolder worldGenHolder;

    public static SavedDataType<RandomiserSaveLoader> TYPE;

    private ServerRandomiser serverRandomiser;
    private HashMap<UUID, Modules> clientRandomisers;

    public RandomiserSaveLoader(MinecraftServer server) {
        this.serverRandomiser = new ServerRandomiser(getWorldgenModulesOrElseCreate(), server);
        this.clientRandomisers = new HashMap<>();
        setDirty();
    }

    private RandomiserSaveLoader(ServerRandomiser serverRandomiser, HashMap<UUID, Modules> clientRandomisers) {
        this.serverRandomiser = serverRandomiser;
        this.clientRandomisers = clientRandomisers;
        setDirty();
    }

    private static @NonNull Modules getWorldgenModulesOrElseCreate() {
        Modules modules = null;
        if (worldGenHolder != null) {
            modules = worldGenHolder.modules();
        }
        if (modules == null) {
            modules = new Modules();
        }
        return modules;
    }

    public static Codec<RandomiserSaveLoader> codec(MinecraftServer server) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ServerRandomiser.codec(server)
                        .fieldOf("randomiser")
                        .forGetter(RandomiserSaveLoader::getServerRandomiser),
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, Modules.CODEC)
                        .xmap(HashMap::new, map -> map)
                        .fieldOf("client_randomisers")
                        .forGetter(RandomiserSaveLoader::getClientRandomisers)
        ).apply(instance, RandomiserSaveLoader::new));
    }

    public static RandomiserSaveLoader getServerState(MinecraftServer server) {
        ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);
        if (level == null) throw new IllegalStateException("Tried to get randomiser state before world was loaded");

        SavedDataStorage storage = level.getDataStorage();
        RandomiserSaveLoader state = storage.computeIfAbsent(TYPE);
        state.setDirty();
        if(state.getServerRandomiser() == null) {
            state.serverRandomiser = new ServerRandomiser(getWorldgenModulesOrElseCreate(), server);
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

    public static void setWorldGenHolder(WorldGenHolder worldGenHolder) {
        if (worldGenHolder == null) {
            throw new IllegalArgumentException("Attempted to set a null worldgen holder, this is not allowed");
        }
        RandomiserSaveLoader.worldGenHolder = worldGenHolder;
    }

    public static WorldGenHolder getWorldGenHolder() {
        if(worldGenHolder == null) {
            return new WorldGenHolder(getLastSetRandomiser().getModules());
        }

        return worldGenHolder;
    }

    public ServerRandomiser getServerRandomiser() {
        return serverRandomiser;
    }

    private HashMap<UUID, Modules> getClientRandomisers() {
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
