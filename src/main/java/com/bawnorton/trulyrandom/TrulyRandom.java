package com.bawnorton.trulyrandom;

import com.bawnorton.trulyrandom.event.EventHandler;
import com.bawnorton.trulyrandom.network.Networking;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.ModuleStateTypes;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.registry.TrulyRandomCriteria;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

public class TrulyRandom implements ModInitializer {
    public static final String MOD_ID = "trulyrandom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Version VERSION;

    private static MinecraftServer cachedServer;

    static {
        VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion();
    }

    public static ServerRandomiser getRandomiser(MinecraftServer server) {
        ServerRandomiser randomiser = RandomiserSaveLoader.getServerState(server).getServerRandomiser();
        cachedServer = server;
        return randomiser;
    }

    public static ServerRandomiser getCachedRandomiser() {
        return RandomiserSaveLoader.getLastSetRandomiser();
    }

    public static boolean noRandomiserSet() {
        return !RandomiserSaveLoader.isLastSetRandomiserPresent();
    }

    public static Randomiser getClientRandomiser(MinecraftServer server, UUID uuid) {
        return RandomiserSaveLoader.getServerState(server).getClientRandomiser(uuid, server);
    }

    public static void setClientRandomiser(MinecraftServer server, UUID uuid, Modules modules) {
        RandomiserSaveLoader.getServerState(server).setClientRandomiser(uuid, modules);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static String sid(String path) {
        return id(path).toString();
    }

    public static RandomiserSaveLoader getRandomiserLoader(MinecraftServer server) {
        return RandomiserSaveLoader.getServerState(server);
    }

    public static MinecraftServer getServer() {
        return cachedServer;
    }

    public static void setWorldGenModules(Modules modules) {
        RandomiserSaveLoader.setWorldGenModules(modules);
    }

    @Override
    public void onInitialize() {
        Networking.init();
        EventHandler.init();
        ModuleStateTypes.init();
        TrulyRandomCriteria.init();
        LOGGER.debug("TrulyRandom Initialised");
    }
}

