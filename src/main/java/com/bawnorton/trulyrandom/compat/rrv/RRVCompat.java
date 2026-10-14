package com.bawnorton.trulyrandom.compat.rrv;

import cc.cassian.rrv.common.recipe.ServerRecipeManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public final class RRVCompat {
    public void refresh(MinecraftServer server, boolean recipesChanged) {
        ServerRecipeManager manager = ServerRecipeManager.INSTANCE;
        if (manager.getServer() == server && manager.getVanillaRecipeManager() == server.getRecipeManager()) {
            if (recipesChanged) {
                server.getPlayerList()
                        .getPlayers()
                        .forEach(player
                                -> ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.invoker().onSyncDataPackContents(player, false)
                        );
            }

            manager.reload();
        }
    }
}
