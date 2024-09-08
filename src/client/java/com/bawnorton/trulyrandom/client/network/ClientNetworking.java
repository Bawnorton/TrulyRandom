package com.bawnorton.trulyrandom.client.network;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.random.ClientRandomiser;
import com.bawnorton.trulyrandom.client.screen.TargetedTrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.client.screen.TrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.network.packet.c2s.ProvidedRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.SetServerRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.SetTargetClientRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.*;
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
        ClientPlayNetworking.registerGlobalReceiver(HandshakeS2CPacket.PACKET_ID, ClientNetworking::handleHandshake);
        ClientPlayNetworking.registerGlobalReceiver(OpenRandomiserScreenS2CPacket.PACKET_ID, ClientNetworking::handleOpenRandomiserScreen);
        ClientPlayNetworking.registerGlobalReceiver(OpenTargetedRandomiserScreenS2CPacket.PACKET_ID, ClientNetworking::handleOpenTargetedRandomiserScreen);
        ClientPlayNetworking.registerGlobalReceiver(RequestOtherClientRandomiserS2CPacket.PACKET_ID, ClientNetworking::handleRequestRandomiser);
        ClientPlayNetworking.registerGlobalReceiver(SetClientRandomiserS2CPacket.PACKET_ID, ClientNetworking::handleSetClientRandomiser);
        ClientPlayNetworking.registerGlobalReceiver(SyncLootTableTrackerS2CPacket.PACKET_ID, ClientNetworking::handleSyncLootTableTracker);
        ClientPlayNetworking.registerGlobalReceiver(SyncRecipeTrackerS2CPacket.PACKET_ID, ClientNetworking::handleSyncRecipeTracker);
        ClientPlayNetworking.registerGlobalReceiver(SyncLootDropsS2CPacket.PACKET_ID, ClientNetworking::handleSyncLootDrops);
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

    private static void handleHandshake(HandshakeS2CPacket packet, ClientPlayNetworking.Context context) {
        if (TrulyRandom.VERSION.compareTo(packet.version()) != 0) {
            context.player().sendMessage(Text.translatable("trulyrandom.version_mismatch", packet.version()
                    .getFriendlyString(), TrulyRandom.VERSION.getFriendlyString()), false);
        }
        runCallback(HandshakeS2CPacket.PACKET_ID);
    }

    private static void handleOpenRandomiserScreen(OpenRandomiserScreenS2CPacket packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = MinecraftClient.getInstance();

        client.setScreen(new TrulyRandomSettingsScreen(client.currentScreen, packet.modules(), (modules) -> context.responseSender().sendPacket(new SetServerRandomiserC2SPacket(modules))));
        runCallback(OpenRandomiserScreenS2CPacket.PACKET_ID);
    }

    private static void handleOpenTargetedRandomiserScreen(OpenTargetedRandomiserScreenS2CPacket packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = MinecraftClient.getInstance();

        UUID targetUUID = packet.target();
        PlayerEntity target;
        if (client.world == null) throw new IllegalStateException("Client world is null");

        target = client.world.getPlayerByUuid(targetUUID);
        client.setScreen(new TargetedTrulyRandomSettingsScreen(client.currentScreen, target, packet.modules(), (modules) -> context.responseSender().sendPacket(new SetTargetClientRandomiserC2SPacket(modules, targetUUID))));
        runCallback(OpenTargetedRandomiserScreenS2CPacket.PACKET_ID);
    }

    private static void handleRequestRandomiser(RequestOtherClientRandomiserS2CPacket packet, ClientPlayNetworking.Context context) {
        ClientRandomiser randomiser = TrulyRandomClient.getRandomiser();
        Modules modules = randomiser.getModules();
        context.responseSender().sendPacket(new ProvidedRandomiserC2SPacket(modules, packet.requestee()));
        runCallback(RequestOtherClientRandomiserS2CPacket.PACKET_ID);
    }

    private static void handleSetClientRandomiser(SetClientRandomiserS2CPacket packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = MinecraftClient.getInstance();

        ClientRandomiser randomiser = TrulyRandomClient.getRandomiser();

        boolean blockModelSeedChanged = randomiser.getModules().getSeed(Module.BLOCK_MODELS) != packet.modules().getSeed(Module.BLOCK_MODELS);
        boolean itemModelSeedChanged = randomiser.getModules().getSeed(Module.ITEM_MODELS) != packet.modules().getSeed(Module.ITEM_MODELS);

        randomiser.setModules(packet.modules());
        randomiser.updateBlockModels(client, blockModelSeedChanged);
        randomiser.updateItemModels(client, itemModelSeedChanged);
        runCallback(SetClientRandomiserS2CPacket.PACKET_ID);
    }

    private static void handleSyncLootTableTracker(SyncLootTableTrackerS2CPacket packet, ClientPlayNetworking.Context context) {
        TrulyRandomClient.getRandomiser().setLootTableTracker(packet.tracker());
        runCallback(SyncLootTableTrackerS2CPacket.PACKET_ID);
    }

    private static void handleSyncRecipeTracker(SyncRecipeTrackerS2CPacket packet, ClientPlayNetworking.Context context) {
        TrulyRandomClient.getRandomiser().setRecipeTracker(packet.tracker());
        runCallback(SyncRecipeTrackerS2CPacket.PACKET_ID);
    }

    private static void handleSyncLootDrops(SyncLootDropsS2CPacket packet, ClientPlayNetworking.Context context) {
        LootTableDrops.ALL_DROPS.clear();
        LootTableDrops.ALL_DROPS.putAll(packet.dropsMap());
        runCallback(SyncLootDropsS2CPacket.PACKET_ID);
    }

    interface RunOnce {
        void run();
    }
}
