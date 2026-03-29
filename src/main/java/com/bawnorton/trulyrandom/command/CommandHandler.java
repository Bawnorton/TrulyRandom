package com.bawnorton.trulyrandom.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

public class CommandHandler {
    private static final TrulyRandomCommand TRULY_RANDOM_COMMAND;

    static {
        TRULY_RANDOM_COMMAND = new TrulyRandomCommand();
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        TRULY_RANDOM_COMMAND.register(dispatcher, context);
    }
}
