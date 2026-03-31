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
import com.bawnorton.trulyrandom.random.module.state.LootModuleState;
import com.bawnorton.trulyrandom.random.module.state.RecipeModuleState;
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
        serverboundPlay.register(ServerboundHandshakePacket.TYPE, ServerboundHandshakePacket.STREAM_CODEC);
        serverboundPlay.register(ServerboundProvidedRandomiserPacket.TYPE, ServerboundProvidedRandomiserPacket.STREAM_CODEC);
        serverboundPlay.register(ServerboundSetServerRandomiserPacket.TYPE, ServerboundSetServerRandomiserPacket.STREAM_CODEC);
        serverboundPlay.register(ServerboundSetTargetClientRandomiserPacket.TYPE, ServerboundSetTargetClientRandomiserPacket.STREAM_CODEC);
        serverboundPlay.register(ServerboundRequestServerRandomiserPacket.TYPE, ServerboundRequestServerRandomiserPacket.STREAM_CODEC);

        PayloadTypeRegistry<RegistryFriendlyByteBuf> clientboundPlay = PayloadTypeRegistry.clientboundPlay();
        clientboundPlay.register(ClientboundHandshakePacket.TYPE, ClientboundHandshakePacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundOpenRandomiserScreenPacket.TYPE, ClientboundOpenRandomiserScreenPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundOpenTargetedRandomiserScreenPacket.TYPE, ClientboundOpenTargetedRandomiserScreenPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundRequestOtherClientRandomiserPacket.TYPE, ClientboundRequestOtherClientRandomiserPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundSetClientRandomiserPacket.TYPE, ClientboundSetClientRandomiserPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundSyncLootTableTrackerPacket.TYPE, ClientboundSyncLootTableTrackerPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundSyncRecipeTrackerPacket.TYPE, ClientboundSyncRecipeTrackerPacket.STREAM_CODEC);
        clientboundPlay.register(ClientboundSyncLootDropsPacket.TYPE, ClientboundSyncLootDropsPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerboundHandshakePacket.TYPE, Networking::handleHandshake);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundProvidedRandomiserPacket.TYPE, Networking::handleProvidedRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundSetServerRandomiserPacket.TYPE, Networking::handleSetServerRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundSetTargetClientRandomiserPacket.TYPE, Networking::handleSetTargetClientRandomiser);
        ServerPlayNetworking.registerGlobalReceiver(ServerboundRequestServerRandomiserPacket.TYPE, Networking::handleRequestRandomiser);
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
        // other minecraft requesting the randomiser data
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
        boolean recipeSettingsChanged = !randomiser.getModules().getState(Module.RECIPES, RecipeModuleState.class).getEnabledRecipeTypes().equals(packet.modules().getState(Module.RECIPES, RecipeModuleState.class).getEnabledRecipeTypes());
        boolean lootSettingsChanged = randomiser.getModules().getState(Module.LOOT_TABLES, LootModuleState.class).useOtherLootTables() != packet.modules().getState(Module.LOOT_TABLES, LootModuleState.class).useOtherLootTables();

        randomiser.setModules(packet.modules());
        randomiser.updateLoot(server, lootSeedChanged, lootSettingsChanged);
        randomiser.updateRecipes(server, recipeSeedChanged, recipeSettingsChanged);
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
