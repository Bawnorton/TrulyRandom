package com.bawnorton.trulyrandom.client.network;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.RecipeBookScreenExtender;
import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import com.bawnorton.trulyrandom.client.random.ClientRandomiser;
import com.bawnorton.trulyrandom.client.screen.TargetedTrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.client.screen.TrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundProvidedRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundSetServerRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundSetTargetClientRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.*;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.state.BlockModelModuleState;
import com.bawnorton.trulyrandom.random.module.state.RecipeModuleState;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientNetworking {
    private static final Map<CustomPacketPayload.Type<?>, RunOnce> recievedCallback = new HashMap<>();

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundHandshakePacket.TYPE, ClientNetworking::handleHandshake);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundOpenRandomiserScreenPacket.TYPE, ClientNetworking::handleOpenRandomiserScreen);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundOpenTargetedRandomiserScreenPacket.TYPE, ClientNetworking::handleOpenTargetedRandomiserScreen);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundRequestOtherClientRandomiserPacket.TYPE, ClientNetworking::handleRequestRandomiser);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSetClientRandomiserPacket.TYPE, ClientNetworking::handleSetClientRandomiser);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncLootTableTrackerPacket.TYPE, ClientNetworking::handleSyncLootTableTracker);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncRecipeTrackerPacket.TYPE, ClientNetworking::handleSyncRecipeTracker);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncLootDropsPacket.TYPE, ClientNetworking::handleSyncLootDrops);
    }

    public static void registerRecievedCallback(CustomPacketPayload.Type<?> packetId, Runnable callback) {
        recievedCallback.put(packetId, () -> {
            callback.run();
            recievedCallback.remove(packetId);
        });
    }

    private static void runCallback(CustomPacketPayload.Type<?> packetId) {
        RunOnce callback = recievedCallback.get(packetId);
        if (callback != null) {
            callback.run();
        }
    }

    private static void handleHandshake(ClientboundHandshakePacket packet, ClientPlayNetworking.Context context) {
        if (TrulyRandom.VERSION.compareTo(packet.version()) != 0) {
            context.player().sendSystemMessage(Component.translatable("trulyrandom.version_mismatch", packet.version().getFriendlyString(), TrulyRandom.VERSION.getFriendlyString()));
        } else {
            context.player().sendSystemMessage(Component.translatable("trulyrandom.join", packet.version().getFriendlyString(), KeybindManager.OPEN_RANDOMISER_GUI.getKeybind().getTranslatedKeyMessage().getString()));
        }
        runCallback(ClientboundHandshakePacket.TYPE);
    }

    private static void handleOpenRandomiserScreen(ClientboundOpenRandomiserScreenPacket packet, ClientPlayNetworking.Context context) {
        Minecraft minecraft = Minecraft.getInstance();

        minecraft.setScreen(new TrulyRandomSettingsScreen(minecraft.screen, packet.modules(), (modules) -> context.responseSender().sendPacket(new ServerboundSetServerRandomiserPacket(modules))));
        runCallback(ClientboundOpenRandomiserScreenPacket.TYPE);
    }

    private static void handleOpenTargetedRandomiserScreen(ClientboundOpenTargetedRandomiserScreenPacket packet, ClientPlayNetworking.Context context) {
        Minecraft minecraft = Minecraft.getInstance();

        UUID targetUUID = packet.target();
        Player target;
        if (minecraft.level == null) throw new IllegalStateException("Client world is null");

        target = minecraft.level.getPlayerByUUID(targetUUID);
        minecraft.setScreen(new TargetedTrulyRandomSettingsScreen(minecraft.screen, target, packet.modules(), (modules) -> context.responseSender().sendPacket(new ServerboundSetTargetClientRandomiserPacket(modules, targetUUID))));
        runCallback(ClientboundOpenTargetedRandomiserScreenPacket.TYPE);
    }

    private static void handleRequestRandomiser(ClientboundRequestOtherClientRandomiserPacket packet, ClientPlayNetworking.Context context) {
        ClientRandomiser randomiser = TrulyRandomClient.getRandomiser();
        Modules modules = randomiser.getModules();
        context.responseSender().sendPacket(new ServerboundProvidedRandomiserPacket(modules, packet.requestee()));
        runCallback(ClientboundRequestOtherClientRandomiserPacket.TYPE);
    }

    private static void handleSetClientRandomiser(ClientboundSetClientRandomiserPacket packet, ClientPlayNetworking.Context context) {
        Minecraft minecraft = Minecraft.getInstance();

        ClientRandomiser randomiser = TrulyRandomClient.getRandomiser();

        boolean blockModelSeedChanged = randomiser.getModules().getSeed(Module.BLOCK_MODELS) != packet.modules().getSeed(Module.BLOCK_MODELS);
        boolean itemModelSeedChanged = randomiser.getModules().getSeed(Module.ITEM_MODELS) != packet.modules().getSeed(Module.ITEM_MODELS);
        boolean blockModelSettingsChanged = randomiser.getModules().getState(Module.BLOCK_MODELS, BlockModelModuleState.class).isIgnoreModelOcclusion() != packet.modules().getState(Module.BLOCK_MODELS, BlockModelModuleState.class).isIgnoreModelOcclusion();
        blockModelSettingsChanged |= randomiser.getModules().getState(Module.BLOCK_MODELS, BlockModelModuleState.class).isIgnoreStateProperties() != packet.modules().getState(Module.BLOCK_MODELS, BlockModelModuleState.class).isIgnoreStateProperties();

        randomiser.setModules(packet.modules());
        randomiser.updateBlockModels(minecraft, blockModelSeedChanged || blockModelSettingsChanged);
        randomiser.updateItemModels(minecraft, itemModelSeedChanged);
        runCallback(ClientboundSetClientRandomiserPacket.TYPE);
    }

    private static void handleSyncLootTableTracker(ClientboundSyncLootTableTrackerPacket packet, ClientPlayNetworking.Context context) {
        TrulyRandomClient.getRandomiser().setLootTableTracker(packet.tracker());
        runCallback(ClientboundSyncLootTableTrackerPacket.TYPE);
    }

    private static void handleSyncRecipeTracker(ClientboundSyncRecipeTrackerPacket packet, ClientPlayNetworking.Context context) {
        TrulyRandomClient.getRandomiser().setRecipeTracker(packet.tracker());
        if(context.client().screen instanceof RecipeBookScreenExtender extender) {
            extender.trulyrandom$refreshResults();
        }
        runCallback(ClientboundSyncRecipeTrackerPacket.TYPE);
    }

    private static void handleSyncLootDrops(ClientboundSyncLootDropsPacket packet, ClientPlayNetworking.Context context) {
        LootTableDrops.ALL_DROPS.clear();
        LootTableDrops.ALL_DROPS.putAll(packet.dropsMap());
        runCallback(ClientboundSyncLootDropsPacket.TYPE);
    }

    interface RunOnce {
        void run();
    }
}
