package com.bawnorton.trulyrandom.network;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundHandshakePacket;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundProvidedRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundRequestServerRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundSetServerRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundSetTargetClientRandomiserPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.*;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.RecipeModuleState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class Networking {
    public static void init() {
        PayloadTypeRegistry<RegistryFriendlyByteBuf> serverboundPlay = PayloadTypeRegistry.serverboundPlay();
        serverboundPlay.register(ServerboundHandshakePacket.PACKET_ID, ServerboundHandshakePacket.STREAM_CODEC);
        serverboundPlay.register(ServerboundProvidedRandomiserPacket.PACKET_ID, ServerboundProvidedRandomiserPacket.STREAM_CODEC);
        serverboundPlay.register(ServerboundSetServerRandomiserPacket.PACKET_ID, ServerboundSetServerRandomiserPacket.STREAM_CODEC);
        serverboundPlay.register(ServerboundSetTargetClientRandomiserPacket.PACKET_ID, ServerboundSetTargetClientRandomiserPacket.STREAM_CODEC);
        serverboundPlay.register(ServerboundRequestServerRandomiserPacket.PACKET_ID, ServerboundRequestServerRandomiserPacket.STREAM_CODEC);

        PayloadTypeRegistry<RegistryFriendlyByteBuf> clientboundPlay = PayloadTypeRegistry.clientboundPlay();
        clientboundPlay.register(ClientboundHandshakePacket.PACKET_ID, ClientboundHandshakePacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundOpenRandomiserScreenPacket.PACKET_ID, ClientboundOpenRandomiserScreenPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundOpenTargetedRandomiserScreenPacket.PACKET_ID, ClientboundOpenTargetedRandomiserScreenPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundRequestOtherClientRandomiserPacket.PACKET_ID, ClientboundRequestOtherClientRandomiserPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundSetClientRandomiserPacket.PACKET_ID, ClientboundSetClientRandomiserPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundSyncLootTableTrackerPacket.PACKET_ID, ClientboundSyncLootTableTrackerPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundSyncRecipeTrackerPacket.PACKET_ID, ClientboundSyncRecipeTrackerPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundSyncLootDropsPacket.PACKET_ID, ClientboundSyncLootDropsPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerboundHandshakePacket.PACKET_ID, Networking::handleHandshake);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundProvidedRandomiserPacket.PACKET_ID, Networking::handleProvidedRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundSetServerRandomiserPacket.PACKET_ID, Networking::handleSetServerRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundSetTargetClientRandomiserPacket.PACKET_ID, Networking::handleSetTargetClientRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundRequestServerRandomiserPacket.PACKET_ID, Networking::handleRequestRandomiser);
    }

    private static void handleRequestRandomiser(ServerboundRequestServerRandomiserPacket ServerboundrequestServerRandomiserPacket, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        MinecraftServer server = player.level().getServer();
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        context.responseSender().sendPacket(new ClientboundSetClientRandomiserPacket(randomiser.getModules()));
    }

    private static void handleHandshake(ServerboundHandshakePacket packet, ServerPlayNetworking.Context context) {
        context.responseSender().sendPacket(new ClientboundHandshakePacket(TrulyRandom.VERSION));
    }

    private static void handleProvidedRandomiser(ServerboundProvidedRandomiserPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        MinecraftServer server = player.level().getServer();
        // other client requesting the randomiser data
        UUID requestee = packet.requestee();
        ServerPlayer requesteePlayer = server.getPlayerList().getPlayer(requestee);
        if (requesteePlayer == null) {
            player.sendSystemMessage(Component.translatable("trulyrandom.no_player_found", requestee.toString()), false);
            return;
        }
        ServerPlayNetworking.send(requesteePlayer, new ClientboundOpenTargetedRandomiserScreenPacket(player.getUUID(), packet.modules()));
    }

    private static void handleSetServerRandomiser(ServerboundSetServerRandomiserPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        MinecraftServer server = player.level().getServer();
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);

        boolean lootSeedChanged = randomiser.getModules().getSeed(Module.LOOT_TABLES) != packet.modules().getSeed(Module.LOOT_TABLES);
        boolean recipeSeedChanged = randomiser.getModules().getSeed(Module.RECIPES) != packet.modules().getSeed(Module.RECIPES);
        boolean tradeSeedChanged = randomiser.getModules().getSeed(Module.TRADES) != packet.modules().getSeed(Module.TRADES);
        boolean enabledRecipeTypesChanged = !randomiser.getModules().getState(Module.RECIPES, RecipeModuleState.class).getEnabledRecipeTypes().equals(packet.modules().getState(Module.RECIPES, RecipeModuleState.class).getEnabledRecipeTypes());

        randomiser.setModules(packet.modules());
        randomiser.updateLoot(server, lootSeedChanged);
        randomiser.updateRecipes(server, recipeSeedChanged, enabledRecipeTypesChanged);
        randomiser.updateTrades(server, tradeSeedChanged);
        randomiser.updateClients(server);
    }

    private static void handleSetTargetClientRandomiser(ServerboundSetTargetClientRandomiserPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        MinecraftServer server = player.level().getServer();
        UUID target = packet.target();
        ServerPlayer targetPlayer = server.getPlayerList().getPlayer(target);
        if (targetPlayer == null) {
            player.sendSystemMessage(Component.translatable("trulyrandom.no_player_found", target.toString()), false);
            return;
        }
        Modules modules = packet.modules();
        TrulyRandom.setClientRandomiser(server, target, modules);
        ServerPlayNetworking.send(targetPlayer, new ClientboundSetClientRandomiserPacket(modules));
    }
}
