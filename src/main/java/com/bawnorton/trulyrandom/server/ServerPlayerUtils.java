package com.bawnorton.trulyrandom.server;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.world.item.component.ResolvableProfile;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class ServerPlayerUtils {
    public static DataResult<Component> getPlayerName(MinecraftServer server, UUID playerId) {
        PlayerList playerList = server.getPlayerList();
        ServerPlayer player = playerList.getPlayer(playerId);
        if (player == null) {
            try {
                return getOfflinePlayerName(server, playerId);
            } catch (IOException | NullPointerException e) {
                return DataResult.error(Component.translatable("trulyrandom.no_player_found", playerId)::getString);
            }
        }
        return DataResult.success(player.getDisplayName());
    }

    private static DataResult<Component> getOfflinePlayerName(MinecraftServer server, UUID playerId) throws IOException {
        ResolvableProfile unresolved = ResolvableProfile.createUnresolved(playerId);
        ProfileResolver profileResolver = server.services().profileResolver();
        CompletableFuture<GameProfile> future = unresolved.resolveProfile(profileResolver);
        try {
            GameProfile profile = future.get(5, TimeUnit.SECONDS);
            String name = profile.name();
            if (name.isEmpty()) {
                return DataResult.error(Component.translatable("trulyrandom.profile_not_resolved", playerId)::getString);
            }
            return DataResult.success(Component.literal(name));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            TrulyRandom.LOGGER.error("Failed to fetch profile", e);
            return DataResult.error(Component.translatable("trulyrandom.no_player_found", playerId)::getString);
        }
    }
}
