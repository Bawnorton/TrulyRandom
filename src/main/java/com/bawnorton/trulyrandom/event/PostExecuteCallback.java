package com.bawnorton.trulyrandom.event;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.command.ServerCommandSource;

public interface PostExecuteCallback {
    Event<PostExecuteCallback> EVENT = EventFactory.createArrayBacked(PostExecuteCallback.class, (listeners) -> (source) -> {
        for (PostExecuteCallback listener : listeners) {
            listener.postExecute(source);
        }
    });

    void postExecute(ServerCommandSource source) throws CommandSyntaxException;
}