package com.bawnorton.trulyrandom.event;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.command.CommandHandler;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentType;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentTypeSerializer;
import com.bawnorton.trulyrandom.network.packet.s2c.SetClientRandomiserS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.SyncLootDropsS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.SyncLootTableTrackerS2CPacket;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.loot.LootRandomiser;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CommandHandler.register(dispatcher));
    }

    private static void registerServerEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
            randomiser.updateLoot(server, false);
            randomiser.updateRecipes(server, false);
            LootTableDrops.populate(server.getReloadableRegistries().getRegistryManager().get(RegistryKeys.LOOT_TABLE));
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
            ServerPlayNetworking.send(handler.player, new SetClientRandomiserS2CPacket(randomiser.getModules()));

            LootRandomiser lootRandomiser = randomiser.getLootRandomiser();
            lootRandomiser.initTracker(handler.player);
            LootTableTracker tracker = lootRandomiser.getTracker(handler.player);
            if (tracker == null) return;

            ServerPlayNetworking.send(handler.player, new SyncLootDropsS2CPacket(LootTableDrops.ALL_DROPS));
            ServerPlayNetworking.send(handler.player, new SyncLootTableTrackerS2CPacket(tracker));
        });
    }

    private static void registerTickEvents() {
        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if(!TrulyRandom.isCachedRandomiserSet()) return;

            ServerRandomiser randomiser = TrulyRandom.getCachedRandomiser();
            PlayerManager playerManager = world.getServer().getPlayerManager();
            playerManager.getPlayerList().forEach(player -> {
                LootTableTracker tracker = randomiser.getLootRandomiser().getTracker(player);
                if (tracker == null || !tracker.isDirty()) return;

                TrulyRandom.getRandomiserLoader(world.getServer()).markDirty();
                tracker.setDirty(false);
                ServerPlayNetworking.send(player, new SyncLootTableTrackerS2CPacket(tracker));
            });
        });
    }
}
