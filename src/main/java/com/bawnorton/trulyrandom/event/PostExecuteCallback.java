package com.bawnorton.trulyrandom.event;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.commands.CommandSourceStack;

public interface PostExecuteCallback {
    Event<PostExecuteCallback> EVENT = EventFactory.createArrayBacked(PostExecuteCallback.class, (listeners) -> (source) -> {
        for (PostExecuteCallback listener : listeners) {
            listener.postExecute(source);
        }
    });

    void postExecute(CommandSourceStack source) throws CommandSyntaxException;
}