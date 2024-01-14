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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class ClientNetworking {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(HandshakeS2CPacket.TYPE, ClientNetworking::handleHandshake);
        ClientPlayNetworking.registerGlobalReceiver(OpenRandomiserScreenS2CPacket.TYPE, ClientNetworking::handleOpenRandomiserScreen);
        ClientPlayNetworking.registerGlobalReceiver(OpenTargetedRandomiserScreenS2CPacket.TYPE, ClientNetworking::handleOpenTargetedRandomiserScreen);
        ClientPlayNetworking.registerGlobalReceiver(RequestRandomiserS2CPacket.TYPE, ClientNetworking::handleRequestRandomiser);
        ClientPlayNetworking.registerGlobalReceiver(SetClientRandomiserS2CPacket.TYPE, ClientNetworking::handleSetClientRandomiser);
    }


    private static void handleHandshake(HandshakeS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        if (TrulyRandom.VERSION.compareTo(packet.version()) != 0) {
            player.sendMessage(Text.translatable("trulyrandom.version_mismatch", packet.version()
                    .getFriendlyString(), TrulyRandom.VERSION.getFriendlyString()), false);
        }
    }

    private static void handleOpenRandomiserScreen(OpenRandomiserScreenS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        MinecraftClient client = MinecraftClient.getInstance();

        client.setScreen(new TrulyRandomSettingsScreen(client.currentScreen, packet.modules(), (modules) -> sender.sendPacket(new SetServerRandomiserC2SPacket(modules))));
    }

    private static void handleOpenTargetedRandomiserScreen(OpenTargetedRandomiserScreenS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        MinecraftClient client = MinecraftClient.getInstance();

        UUID targetUUID = packet.target();
        PlayerEntity target;
        if (client.world == null) throw new IllegalStateException("Client world is null");

        target = client.world.getPlayerByUuid(targetUUID);
        client.setScreen(new TargetedTrulyRandomSettingsScreen(client.currentScreen, target, packet.modules(), (modules) -> sender.sendPacket(new SetTargetClientRandomiserC2SPacket(modules, targetUUID))));
    }

    private static void handleRequestRandomiser(RequestRandomiserS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        ClientRandomiser randomiser = TrulyRandomClient.getRandomiser();
        Modules modules = randomiser.getModules();
        sender.sendPacket(new ProvidedRandomiserC2SPacket(modules, packet.requestee()));
    }

    private static void handleSetClientRandomiser(SetClientRandomiserS2CPacket packet, ClientPlayerEntity player, PacketSender sender) {
        MinecraftClient client = MinecraftClient.getInstance();

        ClientRandomiser randomiser = TrulyRandomClient.getRandomiser();

        boolean blockModelSeedChanged = randomiser.getModules().getSeed(Module.BLOCK_MODELS) != packet.modules().getSeed(Module.BLOCK_MODELS);
        boolean itemModelSeedChanged = randomiser.getModules().getSeed(Module.ITEM_MODELS) != packet.modules().getSeed(Module.ITEM_MODELS);

        randomiser.setModules(packet.modules());
        randomiser.updateBlockModels(client, blockModelSeedChanged);
        randomiser.updateItemModels(client, itemModelSeedChanged);
    }
}
