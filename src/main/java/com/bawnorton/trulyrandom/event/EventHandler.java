package com.bawnorton.trulyrandom.event;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.command.CommandHandler;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentType;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentTypeSerializer;
import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundSetClientRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundSyncLootDropsPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundSyncLootTableTrackerPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundSyncRecipeTrackerPacket;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.loot.LootRandomiser;
import com.bawnorton.trulyrandom.random.recipe.RecipeRandomiser;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.loot.LootTable;

public class EventHandler {
    public static void init() {
        registerCommands();
        registerServerEvents();
        registerTickEvents();
    }

    private static void registerCommands() {
        ArgumentTypeRegistry.registerArgumentType(TrulyRandom.id("set_string"), SetStringArgumentType.class, new SetStringArgumentTypeSerializer());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CommandHandler.register(dispatcher, registryAccess));
    }

    private static void registerServerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
            randomiser.updateLoot(server, false);
            randomiser.updateRecipes(server, false);
            randomiser.updateTrades(server, false);
            LootTableDrops.populate((Registry<LootTable>) server.reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, _, server) -> {
            ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
            ServerPlayNetworking.send(handler.player, new ClientboundSetClientRandomiserPacket(randomiser.getModules()));

            LootRandomiser lootRandomiser = randomiser.getLootRandomiser();
            RecipeRandomiser recipeRandomiser = randomiser.getRecipeRandomiser();
            lootRandomiser.initTracker(handler.player);
            recipeRandomiser.initTracker(handler.player);
            LootTableTracker lootTableTracker = lootRandomiser.getTracker(handler.player);
            RecipeTracker recipeTracker = recipeRandomiser.getTracker(handler.player);
            if(lootTableTracker == null) {
                lootTableTracker = new LootTableTracker();
                lootTableTracker.setTeam(((TeamMember) handler.player).trulyrandom$getTeam());
            }
            if(recipeTracker == null) {
                recipeTracker = new RecipeTracker();
                recipeTracker.setTeam(((TeamMember) handler.player).trulyrandom$getTeam());
            }

            ServerPlayNetworking.send(handler.player, new ClientboundSyncLootDropsPacket(LootTableDrops.ALL_DROPS));
            ServerPlayNetworking.send(handler.player, new ClientboundSyncLootTableTrackerPacket(lootTableTracker));
            ServerPlayNetworking.send(handler.player, new ClientboundSyncRecipeTrackerPacket(recipeTracker));
        });
    }

    private static void registerTickEvents() {
        ServerTickEvents.START_LEVEL_TICK.register(world -> {
            if(TrulyRandom.noRandomiserSet()) return;

            ServerRandomiser randomiser = TrulyRandom.getCachedRandomiser();
            PlayerList playerList = world.getServer().getPlayerList();
            playerList.getPlayers().forEach(player -> {
                LootTableTracker lootTableTracker = randomiser.getLootRandomiser().getTracker(player);
                if (lootTableTracker != null && lootTableTracker.isDirty()) {
                    TrulyRandom.getRandomiserLoader(world.getServer()).setDirty();
                    lootTableTracker.setDirty(false);
                    ServerPlayNetworking.send(player, new ClientboundSyncLootTableTrackerPacket(lootTableTracker));
                }

                RecipeTracker recipeTracker = randomiser.getRecipeRandomiser().getTracker(player);
                if (recipeTracker != null && recipeTracker.isDirty()) {
                    TrulyRandom.getRandomiserLoader(world.getServer()).setDirty();
                    recipeTracker.setDirty(false);
                    ServerPlayNetworking.send(player, new ClientboundSyncRecipeTrackerPacket(recipeTracker));
                }
            });
        });
    }
}
