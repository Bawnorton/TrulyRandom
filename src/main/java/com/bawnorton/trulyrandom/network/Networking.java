package com.bawnorton.trulyrandom.network;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.packet.c2s.HandshakeC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.ProvidedRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.RequestServerRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.SetServerRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.SetTargetClientRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.*;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.RecipeModuleState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.UUID;

public class Networking {
    public static void init() {
        PayloadTypeRegistry<RegistryByteBuf> playC2S = PayloadTypeRegistry.playC2S();
        playC2S.register(HandshakeC2SPacket.PACKET_ID, HandshakeC2SPacket.PACKET_CODEC);
        playC2S.register(ProvidedRandomiserC2SPacket.PACKET_ID, ProvidedRandomiserC2SPacket.PACKET_CODEC);
        playC2S.register(SetServerRandomiserC2SPacket.PACKET_ID, SetServerRandomiserC2SPacket.PACKET_CODEC);
        playC2S.register(SetTargetClientRandomiserC2SPacket.PACKET_ID, SetTargetClientRandomiserC2SPacket.PACKET_CODEC);
        playC2S.register(RequestServerRandomiserC2SPacket.PACKET_ID, RequestServerRandomiserC2SPacket.PACKET_CODEC);

        PayloadTypeRegistry<RegistryByteBuf> playS2C = PayloadTypeRegistry.playS2C();
        playS2C.register(HandshakeS2CPacket.PACKET_ID, HandshakeS2CPacket.PACKET_CODEC);
        playS2C.register(OpenRandomiserScreenS2CPacket.PACKET_ID, OpenRandomiserScreenS2CPacket.PACKET_CODEC);
        playS2C.register(OpenTargetedRandomiserScreenS2CPacket.PACKET_ID, OpenTargetedRandomiserScreenS2CPacket.PACKET_CODEC);
        playS2C.register(RequestOtherClientRandomiserS2CPacket.PACKET_ID, RequestOtherClientRandomiserS2CPacket.PACKET_CODEC);
        playS2C.register(SetClientRandomiserS2CPacket.PACKET_ID, SetClientRandomiserS2CPacket.PACKET_CODEC);
        playS2C.register(SyncLootTableTrackerS2CPacket.PACKET_ID, SyncLootTableTrackerS2CPacket.PACKET_CODEC);
        playS2C.register(SyncRecipeTrackerS2CPacket.PACKET_ID, SyncRecipeTrackerS2CPacket.PACKET_CODEC);
        playS2C.register(SyncLootDropsS2CPacket.PACKET_ID, SyncLootDropsS2CPacket.PACKET_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(HandshakeC2SPacket.PACKET_ID, Networking::handleHandshake);
        ServerPlayNetworking.registerGlobalReceiver(ProvidedRandomiserC2SPacket.PACKET_ID, Networking::handleProvidedRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(SetServerRandomiserC2SPacket.PACKET_ID, Networking::handleSetServerRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(SetTargetClientRandomiserC2SPacket.PACKET_ID, Networking::handleSetTargetClientRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(RequestServerRandomiserC2SPacket.PACKET_ID, Networking::handleRequestRandomiser);
    }

    private static void handleRequestRandomiser(RequestServerRandomiserC2SPacket requestServerRandomiserC2SPacket, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();
        ServerRandomiser randomiser = TrulyRandom.getRandomiser(server);
        context.responseSender().sendPacket(new SetClientRandomiserS2CPacket(randomiser.getModules()));
    }

    private static void handleHandshake(HandshakeC2SPacket packet, ServerPlayNetworking.Context context) {
        context.responseSender().sendPacket(new HandshakeS2CPacket(TrulyRandom.VERSION));
    }

    private static void handleProvidedRandomiser(ProvidedRandomiserC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();
        if(server == null) return;

        // other client requesting the randomiser data
        UUID requestee = packet.requestee();
        ServerPlayerEntity requesteePlayer = server.getPlayerManager().getPlayer(requestee);
        if (requesteePlayer == null) {
            player.sendMessage(Text.translatable("trulyrandom.no_player_found", requestee.toString()), false);
            return;
        }
        ServerPlayNetworking.send(requesteePlayer, new OpenTargetedRandomiserScreenS2CPacket(player.getUuid(), packet.modules()));
    }

    private static void handleSetServerRandomiser(SetServerRandomiserC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();
        if(server == null) return;

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

    private static void handleSetTargetClientRandomiser(SetTargetClientRandomiserC2SPacket packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        MinecraftServer server = player.getServer();
        if(server == null) return;

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
