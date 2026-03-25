package com.bawnorton.trulyrandom.api;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundSetClientRandomiserPacket;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("unused")
public final class TrulyRandomApi {
    public static void randomiseBlockModels(ServerPlayer player, boolean randomSeed) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.level().getServer(), player.getUUID());
        Modules modules = randomiser.getCopiedModules();
        if(randomSeed) modules.randomSeed(Module.BLOCK_MODELS);
        modules.setEnabled(Module.BLOCK_MODELS);
        updateClient(player, modules);
    }

    public static void resetBlockModels(ServerPlayer player) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.level().getServer(), player.getUUID());
        Modules modules = randomiser.getCopiedModules();
        modules.setDisabled(Module.BLOCK_MODELS);
        updateClient(player, modules);
    }

    public static void randomiseItemModels(ServerPlayer player, boolean randomSeed) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.level().getServer(), player.getUUID());
        Modules modules = randomiser.getCopiedModules();
        if(randomSeed) modules.randomSeed(Module.ITEM_MODELS);
        modules.setEnabled(Module.ITEM_MODELS);
        updateClient(player, modules);
    }

    public static void resetItemModels(ServerPlayer player) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.level().getServer(), player.getUUID());
        Modules modules = randomiser.getCopiedModules();
        modules.setDisabled(Module.ITEM_MODELS);
        updateClient(player, modules);
    }

    public static void randomiseAllModels(ServerPlayer player, boolean randomSeed) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.level().getServer(), player.getUUID());
        Modules modules = randomiser.getCopiedModules();
        if(randomSeed) {
            modules.randomSeed(Module.BLOCK_MODELS);
            modules.randomSeed(Module.ITEM_MODELS);
        }
        modules.setEnabled(Module.BLOCK_MODELS);
        modules.setEnabled(Module.ITEM_MODELS);
        updateClient(player, modules);
    }

    public static void resetAllModels(ServerPlayer player) {
        Randomiser randomiser = TrulyRandom.getClientRandomiser(player.level().getServer(), player.getUUID());
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

    public static void randomiseServerTrades(MinecraftServer server, boolean randomSeed) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        if(randomSeed) randomiser.getModules().randomSeed(Module.TRADES);
        randomiser.getModules().setEnabled(Module.TRADES);
        randomiser.updateTrades(server, randomSeed);
    }

    public static void reserServerTrades(MinecraftServer server) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        randomiser.getModules().setDisabled(Module.TRADES);
        randomiser.updateTrades(server, false);
    }

    public static void randomiseAllServer(MinecraftServer server, boolean randomSeed) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        if(randomSeed) randomiser.getModules().randomSeedAll();
        randomiser.getModules().enableAll();
        randomiser.updateClients(server);
        randomiser.updateRecipes(server, randomSeed);
        randomiser.updateLoot(server, randomSeed);
        randomiser.updateTrades(server, randomSeed);
    }

    public static void resetAllServer(MinecraftServer server) {
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        randomiser.getModules().disableAll();
        randomiser.updateClients(server);
        randomiser.updateRecipes(server, false);
        randomiser.updateLoot(server, false);
        randomiser.updateTrades(server, false);
    }

    private static void updateClient(ServerPlayer player, Modules modules) {
        TrulyRandom.setClientRandomiser(player.level().getServer(), player.getUUID(), modules);
        ServerPlayNetworking.send(player, new ClientboundSetClientRandomiserPacket(modules));
    }
}
