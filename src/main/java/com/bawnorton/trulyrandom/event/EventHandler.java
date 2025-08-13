package com.bawnorton.trulyrandom.event;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.command.CommandHandler;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentType;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentTypeSerializer;
import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.network.packet.s2c.SetClientRandomiserS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.SyncLootDropsS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.SyncLootTableTrackerS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.SyncRecipeTrackerS2CPacket;
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
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.PlayerManager;

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
            LootTableDrops.populate((Registry<LootTable>) server.getReloadableRegistries().createRegistryLookup().getOrThrow(RegistryKeys.LOOT_TABLE));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
            ServerPlayNetworking.send(handler.player, new SetClientRandomiserS2CPacket(randomiser.getModules()));

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

            ServerPlayNetworking.send(handler.player, new SyncLootDropsS2CPacket(LootTableDrops.ALL_DROPS));
            ServerPlayNetworking.send(handler.player, new SyncLootTableTrackerS2CPacket(lootTableTracker));
            ServerPlayNetworking.send(handler.player, new SyncRecipeTrackerS2CPacket(recipeTracker));
        });
    }

    private static void registerTickEvents() {
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if(TrulyRandom.noRandomiserSet()) return;

            ServerRandomiser randomiser = TrulyRandom.getCachedRandomiser();
            PlayerManager playerManager = world.getServer().getPlayerManager();
            playerManager.getPlayerList().forEach(player -> {
                LootTableTracker lootTableTracker = randomiser.getLootRandomiser().getTracker(player);
                if (lootTableTracker != null && lootTableTracker.isDirty()) {
                    TrulyRandom.getRandomiserLoader(world.getServer()).markDirty();
                    lootTableTracker.setDirty(false);
                    ServerPlayNetworking.send(player, new SyncLootTableTrackerS2CPacket(lootTableTracker));
                }

                RecipeTracker recipeTracker = randomiser.getRecipeRandomiser().getTracker(player);
                if (recipeTracker != null && recipeTracker.isDirty()) {
                    TrulyRandom.getRandomiserLoader(world.getServer()).markDirty();
                    recipeTracker.setDirty(false);
                    ServerPlayNetworking.send(player, new SyncRecipeTrackerS2CPacket(recipeTracker));
                }
            });
        });
    }
}
