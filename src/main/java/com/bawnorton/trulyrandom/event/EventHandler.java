package com.bawnorton.trulyrandom.event;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.command.CommandHandler;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentType;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentTypeSerializer;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundSetClientRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundSyncLootDropsPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundSyncLootTableTrackerPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundSyncRecipeTrackerPacket;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.loot.LootRandomiser;
import com.bawnorton.trulyrandom.random.recipe.RecipeRandomiser;
import com.bawnorton.trulyrandom.team.Teams;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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

            Player player = handler.player;
            Teams teams = TrulyRandom.getTeams(server);
            teams.findTeamIBelongTo(player.getUUID()).ifPresent(player::trulyrandom$setTeam);

            LootRandomiser lootRandomiser = randomiser.getLootRandomiser();
            RecipeRandomiser recipeRandomiser = randomiser.getRecipeRandomiser();
            LootTableTracker lootTableTracker = lootRandomiser.getTracker(player.trulyrandom$getTeam());
            RecipeTracker recipeTracker = recipeRandomiser.getTracker(player.trulyrandom$getTeam());
            if(lootTableTracker == null) {
                lootTableTracker = new LootTableTracker();
                lootTableTracker.setTeam(player.trulyrandom$getTeam());
            }
            if(recipeTracker == null) {
                recipeTracker = new RecipeTracker();
                recipeTracker.setTeam(player.trulyrandom$getTeam());
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
            MinecraftServer server = world.getServer();
            TrulyRandom.getTeams(server).forEach(team -> {
                LootTableTracker lootTableTracker = randomiser.getLootRandomiser().getTracker(team);
                if (lootTableTracker != null && lootTableTracker.isDirty()) {
                    TrulyRandom.getRandomiserLoader(server).setDirty();
                    team.getOwnerAndPlayers().forEach(uuid -> {
                        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                        if(player == null) return;

                        ServerPlayNetworking.send(player, new ClientboundSyncLootTableTrackerPacket(lootTableTracker));
                    });
                    lootTableTracker.setDirty(false);
                }

                RecipeTracker recipeTracker = randomiser.getRecipeRandomiser().getTracker(team);
                if (recipeTracker != null && recipeTracker.isDirty()) {
                    TrulyRandom.getRandomiserLoader(server).setDirty();
                    team.getOwnerAndPlayers().forEach(uuid -> {
                        ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                        if(player == null) return;

                        ServerPlayNetworking.send(player, new ClientboundSyncRecipeTrackerPacket(recipeTracker));
                    });
                    recipeTracker.setDirty(false);
                }
            });
        });
    }
}
