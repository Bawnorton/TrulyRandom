package com.bawnorton.trulyrandom.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

public class CommandHandler {
    private static final TrulyRandomSettingsCommand trulyRandomSettingsCommand;

    static {
        trulyRandomSettingsCommand = new TrulyRandomSettingsCommand();
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        trulyRandomSettingsCommand.register(dispatcher, registryAccess);
    }
}
