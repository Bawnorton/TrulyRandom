package com.bawnorton.trulyrandom.network;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.packet.c2s.HandshakeC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.ProvidedRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.SetServerRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.SetTargetClientRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.HandshakeS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.OpenTargetedRandomiserScreenS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.SetClientRandomiserS2CPacket;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class Networking {
    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(HandshakeC2SPacket.TYPE, Networking::handleHandshake);
        ServerPlayNetworking.registerGlobalReceiver(ProvidedRandomiserC2SPacket.TYPE, Networking::handleProvidedRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(SetServerRandomiserC2SPacket.TYPE, Networking::handleSetServerRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(SetTargetClientRandomiserC2SPacket.TYPE, Networking::handleSetTargetClientRandomiser);
    }

    private static void handleHandshake(HandshakeC2SPacket packet, ServerPlayerEntity player, PacketSender sender) {
        MinecraftServer server = player.getServer();
        assert server != null;

        Randomiser randomiser = TrulyRandom.getClientRandomiser(server, player.getUuid());
        if(randomiser == null) randomiser = TrulyRandom.getRandomiser(server);
        sender.sendPacket(new HandshakeS2CPacket(TrulyRandom.VERSION));
        sender.sendPacket(new SetClientRandomiserS2CPacket(randomiser.getModules()));
    }

    private static void handleProvidedRandomiser(ProvidedRandomiserC2SPacket packet, ServerPlayerEntity player, PacketSender sender) {
        MinecraftServer server = player.getServer();
        assert server != null;

        UUID requestee = packet.requestee();
        ServerPlayerEntity requesteePlayer = server.getPlayerManager().getPlayer(requestee);
        if (requesteePlayer == null) {
            player.sendMessage(Text.translatable("trulyrandom.no_player_found", requestee), false);
            return;
        }
        ServerPlayNetworking.send(requesteePlayer, new OpenTargetedRandomiserScreenS2CPacket(player.getUuid(), packet.modules()));
    }

    private static void handleSetServerRandomiser(SetServerRandomiserC2SPacket packet, ServerPlayerEntity player, PacketSender sender) {
        MinecraftServer server = player.getServer();
        assert server != null;

        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);

        boolean lootSeedChanged = randomiser.getModules().getSeed(Module.LOOT_TABLES) != packet.modules().getSeed(Module.LOOT_TABLES);
        boolean recipeSeedChanged = randomiser.getModules().getSeed(Module.RECIPES) != packet.modules().getSeed(Module.RECIPES);

        randomiser.setModules(packet.modules());
        randomiser.updateLoot(server, lootSeedChanged);
        randomiser.updateRecipes(server, recipeSeedChanged);
        randomiser.updateClients(server);
    }

    private static void handleSetTargetClientRandomiser(SetTargetClientRandomiserC2SPacket packet, ServerPlayerEntity player, PacketSender sender) {
        MinecraftServer server = player.getServer();
        assert server != null;

        UUID target = packet.target();
        ServerPlayerEntity targetPlayer = server.getPlayerManager().getPlayer(target);
        if (targetPlayer == null) {
            player.sendMessage(Text.translatable("trulyrandom.no_player_found", target.toString()), false);
            return;
        }
        Modules modules = packet.modules();
        TrulyRandom.setClientRandomiser(server, target, modules);
        ServerPlayNetworking.send(targetPlayer, new SetClientRandomiserS2CPacket(modules));
    }
}
