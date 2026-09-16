package com.bawnorton.trulyrandom.world;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.team.Teams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class RandomiserSaveLoader extends SavedData {
    private static ServerRandomiser lastSetRandomiser;
    private static WorldGenHolder worldGenHolder;

    public static SavedDataType<RandomiserSaveLoader> TYPE;

    private Teams teams;
    private ServerRandomiser serverRandomiser;
    private HashMap<UUID, Modules> clientRandomisers;

    public RandomiserSaveLoader(MinecraftServer server) {
        this.teams = Teams.create();
        this.serverRandomiser = new ServerRandomiser(getWorldgenModulesOrElseCreate(), server);
        this.clientRandomisers = new HashMap<>();
        setDirty();
    }

    private RandomiserSaveLoader(Teams teams, ServerRandomiser serverRandomiser, HashMap<UUID, Modules> clientRandomisers) {
        this.teams = teams;
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
                Teams.CODEC.fieldOf("teams").forGetter(RandomiserSaveLoader::getTeams),
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
        Objects.requireNonNull(level, "Tried to get randomiser state before world was loaded");

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
        Objects.requireNonNull(lastSetRandomiser, "Tried to get randomiser state before world was loaded");
        return lastSetRandomiser;
    }

    public static boolean isLastSetRandomiserPresent() {
        return lastSetRandomiser != null;
    }

    public static void setWorldGenHolder(@NotNull WorldGenHolder worldGenHolder) {
        Objects.requireNonNull(worldGenHolder, "Attempted to set a null worldgen holder, this is not allowed");
        if (RandomiserSaveLoader.worldGenHolder != null) {
            TrulyRandom.LOGGER.error("Attempted to replace an existing worldgen holder, this is not allowed", new IllegalStateException());
            return;
        }
        RandomiserSaveLoader.worldGenHolder = worldGenHolder;
    }

    public static WorldGenHolder getWorldGenHolder() {
        return Objects.requireNonNullElseGet(worldGenHolder, () -> new WorldGenHolder(getLastSetRandomiser().getModules()));
    }

    public ServerRandomiser getServerRandomiser() {
        return serverRandomiser;
    }

    public Teams getTeams() {
        return teams;
    }

    private HashMap<UUID, Modules> getClientRandomisers() {
        if (clientRandomisers == null) {
            clientRandomisers = new HashMap<>();
        }
        return clientRandomisers;
    }

    public Randomiser getClientRandomiser(UUID uuid, MinecraftServer server) {
        Modules modules = getClientRandomisers().computeIfAbsent(uuid, _ -> new Modules());

        return new ServerRandomiser(modules, server);
    }

    public void setClientRandomiser(UUID uuid, Modules modules) {
        getClientRandomisers().put(uuid, modules);
    }
}
