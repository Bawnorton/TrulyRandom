package com.bawnorton.trulyrandom.world;

import com.bawnorton.trulyrandom.TrulyRandom;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class RandomiserSaveLoader extends SavedData {
    private static ServerRandomiser lastSetRandomiser;
    private static Modules worldGenModules;

    public static SavedDataType<RandomiserSaveLoader> TYPE;

    private ServerRandomiser serverRandomiser;
    private Map<UUID, Modules> clientRandomisers;

    public RandomiserSaveLoader(MinecraftServer server) {
        this.serverRandomiser = new ServerRandomiser(Objects.requireNonNullElseGet(worldGenModules, Modules::new), server);
        this.clientRandomisers = new HashMap<>();
        setDirty();
    }

    private RandomiserSaveLoader(ServerRandomiser serverRandomiser, Map<UUID, Modules> clientRandomisers) {
        this.serverRandomiser = serverRandomiser;
        this.clientRandomisers = clientRandomisers;
        setDirty();
    }

    public static Codec<RandomiserSaveLoader> codec(MinecraftServer server) {
        return RecordCodecBuilder.create(instance -> instance.group(
                ServerRandomiser.codec(server)
                        .fieldOf("randomiser")
                        .forGetter(RandomiserSaveLoader::getServerRandomiser),
                Codec.unboundedMap(UUIDUtil.CODEC, Modules.CODEC)
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
