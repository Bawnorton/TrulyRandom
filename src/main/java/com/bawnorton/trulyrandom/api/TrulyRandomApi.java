package com.bawnorton.trulyrandom.api;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.packet.s2c.SetClientRandomiserS2CPacket;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

@SuppressWarnings("unused")
public final class TrulyRandomApi {
    public static void randomiseBlockModels(ServerPlayerEntity player, boolean randomSeed) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.getServer(), player.getUuid());
        Modules modules = randomiser.getCopiedModules();
        if(randomSeed) modules.randomSeed(Module.BLOCK_MODELS);
        modules.setEnabled(Module.BLOCK_MODELS);
        updateClient(player, modules);
    }

    public static void resetBlockModels(ServerPlayerEntity player) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.getServer(), player.getUuid());
        Modules modules = randomiser.getCopiedModules();
        modules.setDisabled(Module.BLOCK_MODELS);
        updateClient(player, modules);
    }

    public static void randomiseItemModels(ServerPlayerEntity player, boolean randomSeed) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.getServer(), player.getUuid());
        Modules modules = randomiser.getCopiedModules();
        if(randomSeed) modules.randomSeed(Module.ITEM_MODELS);
        modules.setEnabled(Module.ITEM_MODELS);
        updateClient(player, modules);
    }

    public static void resetItemModels(ServerPlayerEntity player) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.getServer(), player.getUuid());
        Modules modules = randomiser.getCopiedModules();
        modules.setDisabled(Module.ITEM_MODELS);
        updateClient(player, modules);
    }

    public static void randomiseAllModels(ServerPlayerEntity player, boolean randomSeed) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.getServer(), player.getUuid());
        Modules modules = randomiser.getCopiedModules();
        if(randomSeed) {
            modules.randomSeed(Module.BLOCK_MODELS);
            modules.randomSeed(Module.ITEM_MODELS);
        }
        modules.setEnabled(Module.BLOCK_MODELS);
        modules.setEnabled(Module.ITEM_MODELS);
        updateClient(player, modules);
    }

    public static void resetAllModels(ServerPlayerEntity player) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.getServer(), player.getUuid());
        Modules modules = randomiser.getCopiedModules();
        modules.setDisabled(Module.BLOCK_MODELS);
        modules.setDisabled(Module.ITEM_MODELS);
        updateClient(player, modules);
    }

    public static void randomiseServerBlockModels(MinecraftServer server, boolean randomSeed) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        if(randomSeed) randomiser.getModules().randomSeed(Module.BLOCK_MODELS);
        randomiser.getModules().setEnabled(Module.BLOCK_MODELS);
        randomiser.updateClients(server);
    }

    public static void resetServerBlockModels(MinecraftServer server) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        randomiser.getModules().setDisabled(Module.BLOCK_MODELS);
        randomiser.updateClients(server);
    }

    public static void randomiseServerItemModels(MinecraftServer server, boolean randomSeed) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        if(randomSeed) randomiser.getModules().randomSeed(Module.ITEM_MODELS);
        randomiser.getModules().setEnabled(Module.ITEM_MODELS);
        randomiser.updateClients(server);
    }

    public static void resetServerItemModels(MinecraftServer server) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        randomiser.getModules().setDisabled(Module.ITEM_MODELS);
        randomiser.updateClients(server);
    }

    public static void randomiseAllServerModels(MinecraftServer server, boolean randomSeed) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        if(randomSeed) {
            randomiser.getModules().randomSeed(Module.BLOCK_MODELS);
            randomiser.getModules().randomSeed(Module.ITEM_MODELS);
        }
        randomiser.getModules().setEnabled(Module.BLOCK_MODELS);
        randomiser.getModules().setEnabled(Module.ITEM_MODELS);
        randomiser.updateClients(server);
    }

    public static void resetAllServerModels(MinecraftServer server) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        randomiser.getModules().setDisabled(Module.BLOCK_MODELS);
        randomiser.getModules().setDisabled(Module.ITEM_MODELS);
        randomiser.updateClients(server);
    }

    public static void randomiseServerRecipes(MinecraftServer server, boolean randomSeed) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        if(randomSeed) randomiser.getModules().randomSeed(Module.RECIPES);
        randomiser.getModules().setEnabled(Module.RECIPES);
        randomiser.updateRecipes(server, randomSeed);
    }

    public static void resetServerRecipes(MinecraftServer server) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        randomiser.getModules().setDisabled(Module.RECIPES);
        randomiser.updateRecipes(server, false);
    }

    public static void randomiseServerLootTables(MinecraftServer server, boolean randomSeed) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        if(randomSeed) randomiser.getModules().randomSeed(Module.LOOT_TABLES);
        randomiser.getModules().setEnabled(Module.LOOT_TABLES);
        randomiser.updateLoot(server, randomSeed);
    }

    public static void resetServerLootTables(MinecraftServer server) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        randomiser.getModules().setDisabled(Module.LOOT_TABLES);
        randomiser.updateLoot(server, false);
    }

    public static void randomiseAllServer(MinecraftServer server, boolean randomSeed) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        if(randomSeed) randomiser.getModules().randomSeedAll();
        randomiser.getModules().enableAll();
        randomiser.updateClients(server);
        randomiser.updateRecipes(server, randomSeed);
        randomiser.updateLoot(server, randomSeed);
    }

    public static void resetAllServer(MinecraftServer server) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        randomiser.getModules().disableAll();
        randomiser.updateClients(server);
        randomiser.updateRecipes(server, false);
        randomiser.updateLoot(server, false);
    }

    private static void updateClient(ServerPlayerEntity player, Modules modules) {
        TrulyRandom.setClientRandomiser(player.getServer(), player.getUuid(), modules);
        ServerPlayNetworking.send(player, new SetClientRandomiserS2CPacket(modules));
    }
}
