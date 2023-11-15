package com.bawnorton.trulyrandom;

import com.bawnorton.trulyrandom.event.EventHandler;
import com.bawnorton.trulyrandom.network.Networking;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrulyRandom implements ModInitializer {
    public static final String MOD_ID = "trulyrandom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Version VERSION;

    static {
        VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion();
    }

    public static Randomiser getRandomiser(MinecraftServer server) {
        Randomiser randomiser = RandomiserSaveLoader.getServerState(server).getRandomiser();
        if(!randomiser.initialised()) randomiser.init(server);
        return randomiser;
    }

    @Override
    public void onInitialize() {
        Networking.init();
        EventHandler.init();
        LOGGER.debug("TrulyRandom Initialised");
    }
}