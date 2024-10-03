package com.bawnorton.trulyrandom.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

public class CommandHandler {
    private static final TrulyRandomCommand TRULY_RANDOM_COMMAND;

    static {
        TRULY_RANDOM_COMMAND = new TrulyRandomCommand();
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        TRULY_RANDOM_COMMAND.register(dispatcher, registryAccess);
    }
}
