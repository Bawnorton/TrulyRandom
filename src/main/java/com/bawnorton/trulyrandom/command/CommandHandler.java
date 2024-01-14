package com.bawnorton.trulyrandom.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public class CommandHandler {
    private static final TrulyRandomSettingsCommand trulyRandomSettingsCommand;

    static {
        trulyRandomSettingsCommand = new TrulyRandomSettingsCommand();
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        registerOpenRandomiserScreen(dispatcher);
    }

    private static void registerOpenRandomiserScreen(CommandDispatcher<ServerCommandSource> dispatcher) {
        trulyRandomSettingsCommand.register(dispatcher);
    }
}
