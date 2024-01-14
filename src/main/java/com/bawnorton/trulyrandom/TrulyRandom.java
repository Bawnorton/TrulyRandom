package com.bawnorton.trulyrandom;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import com.bawnorton.trulyrandom.event.EventHandler;
import com.bawnorton.trulyrandom.network.Networking;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class TrulyRandom implements ModInitializer, MixinCanceller {
    public static final String MOD_ID = "trulyrandom";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Version VERSION;

    static {
        VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion();
    }

    public static ServerRandomiser getRandomiser(MinecraftServer server) {
        ServerRandomiser randomiser = RandomiserSaveLoader.getServerState(server).getServerRandomiser();
        if (!randomiser.initialised()) randomiser.init(server);

        return randomiser;
    }

    public static Randomiser getUnsafeRandomiser() {
        return RandomiserSaveLoader.fetchUnsafeRandomiser();
    }

    public static Randomiser getClientRandomiser(MinecraftServer server, UUID uuid) {
        return RandomiserSaveLoader.getServerState(server).getClientRandomiser(uuid);
    }

    public static void setClientRandomiser(MinecraftServer server, UUID uuid, Modules modules) {
        RandomiserSaveLoader.getServerState(server).setClientRandomiser(uuid, modules);
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        Networking.init();
        EventHandler.init();
        LOGGER.debug("TrulyRandom Initialised");
    }

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        return mixinClassName.equals("net.fabricmc.loom.nativesupport.mixin.WindowMixin");
    }
}

