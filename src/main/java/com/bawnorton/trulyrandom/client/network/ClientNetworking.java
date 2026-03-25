package com.bawnorton.trulyrandom.client.network;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.RecipeBookScreenExtender;
import com.bawnorton.trulyrandom.client.random.ClientRandomiser;
import com.bawnorton.trulyrandom.client.screen.TargetedTrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.client.screen.TrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundProvidedRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundSetServerRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundSetTargetClientRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.*;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientNetworking {
    private static final Map<CustomPayload.Id<?>, RunOnce> recievedCallback = new HashMap<>();

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundHandshakePacket.PACKET_ID, ClientNetworking::handleHandshake);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundOpenRandomiserScreenPacket.PACKET_ID, ClientNetworking::handleOpenRandomiserScreen);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundOpenTargetedRandomiserScreenPacket.PACKET_ID, ClientNetworking::handleOpenTargetedRandomiserScreen);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundRequestOtherClientRandomiserPacket.PACKET_ID, ClientNetworking::handleRequestRandomiser);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSetClientRandomiserPacket.PACKET_ID, ClientNetworking::handleSetClientRandomiser);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncLootTableTrackerPacket.PACKET_ID, ClientNetworking::handleSyncLootTableTracker);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncRecipeTrackerPacket.PACKET_ID, ClientNetworking::handleSyncRecipeTracker);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncLootDropsPacket.PACKET_ID, ClientNetworking::handleSyncLootDrops);
    }

    public static void registerRecievedCallback(CustomPayload.Id<?> packetId, Runnable callback) {
        recievedCallback.put(packetId, () -> {
            callback.run();
            recievedCallback.remove(packetId);
        });
    }

    private static void runCallback(CustomPayload.Id<?> packetId) {
        RunOnce callback = recievedCallback.get(packetId);
        if (callback != null) {
            callback.run();
        }
    }

    private static void handleHandshake(ClientboundHandshakePacket packet, ClientPlayNetworking.Context context) {
        if (TrulyRandom.VERSION.compareTo(packet.version()) != 0) {
            context.player().sendMessage(Text.translatable("trulyrandom.version_mismatch", packet.version()
                    .getFriendlyString(), TrulyRandom.VERSION.getFriendlyString()), false);
        }
        runCallback(ClientboundHandshakePacket.PACKET_ID);
    }

    private static void handleOpenRandomiserScreen(ClientboundOpenRandomiserScreenPacket packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = MinecraftClient.getInstance();

        client.setScreen(new TrulyRandomSettingsScreen(client.currentScreen, packet.modules(), (modules) -> context.responseSender().sendPacket(new ServerboundSetServerRandomiserPacket(modules))));
        runCallback(ClientboundOpenRandomiserScreenPacket.PACKET_ID);
    }

    private static void handleOpenTargetedRandomiserScreen(ClientboundOpenTargetedRandomiserScreenPacket packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = MinecraftClient.getInstance();

        UUID targetUUID = packet.target();
        PlayerEntity target;
        if (client.world == null) throw new IllegalStateException("Client world is null");

        target = client.world.getPlayerByUuid(targetUUID);
        client.setScreen(new TargetedTrulyRandomSettingsScreen(client.currentScreen, target, packet.modules(), (modules) -> context.responseSender().sendPacket(new ServerboundSetTargetClientRandomiserPacket(modules, targetUUID))));
        runCallback(ClientboundOpenTargetedRandomiserScreenPacket.PACKET_ID);
    }

    private static void handleRequestRandomiser(ClientboundRequestOtherClientRandomiserPacket packet, ClientPlayNetworking.Context context) {
        ClientRandomiser randomiser = TrulyRandomClient.getRandomiser();
        Modules modules = randomiser.getModules();
        context.responseSender().sendPacket(new ServerboundProvidedRandomiserPacket(modules, packet.requestee()));
        runCallback(ClientboundRequestOtherClientRandomiserPacket.PACKET_ID);
    }

    private static void handleSetClientRandomiser(ClientboundSetClientRandomiserPacket packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = MinecraftClient.getInstance();

        ClientRandomiser randomiser = TrulyRandomClient.getRandomiser();

        boolean blockModelSeedChanged = randomiser.getModules().getSeed(Module.BLOCK_MODELS) != packet.modules().getSeed(Module.BLOCK_MODELS);
        boolean itemModelSeedChanged = randomiser.getModules().getSeed(Module.ITEM_MODELS) != packet.modules().getSeed(Module.ITEM_MODELS);

        randomiser.setModules(packet.modules());
        randomiser.updateBlockModels(client, blockModelSeedChanged);
        randomiser.updateItemModels(client, itemModelSeedChanged);
        runCallback(ClientboundSetClientRandomiserPacket.PACKET_ID);
    }

    private static void handleSyncLootTableTracker(ClientboundSyncLootTableTrackerPacket packet, ClientPlayNetworking.Context context) {
        TrulyRandomClient.getRandomiser().setLootTableTracker(packet.tracker());
        runCallback(ClientboundSyncLootTableTrackerPacket.PACKET_ID);
    }

    private static void handleSyncRecipeTracker(ClientboundSyncRecipeTrackerPacket packet, ClientPlayNetworking.Context context) {
        TrulyRandomClient.getRandomiser().setRecipeTracker(packet.tracker());
        if(context.client().currentScreen instanceof RecipeBookScreenExtender extender) {
            extender.trulyrandom$refreshResults();
        }
        runCallback(ClientboundSyncRecipeTrackerPacket.PACKET_ID);
    }

    private static void handleSyncLootDrops(ClientboundSyncLootDropsPacket packet, ClientPlayNetworking.Context context) {
        LootTableDrops.ALL_DROPS.clear();
        LootTableDrops.ALL_DROPS.putAll(packet.dropsMap());
        runCallback(ClientboundSyncLootDropsPacket.PACKET_ID);
    }

    interface RunOnce {
        void run();
    }
}
