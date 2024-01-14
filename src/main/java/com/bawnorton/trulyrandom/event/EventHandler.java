package com.bawnorton.trulyrandom.event;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.command.CommandHandler;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentType;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentTypeSerializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.Identifier;

public class EventHandler {
    public static void init() {
        registerCommands();
    }

    private static void registerCommands() {
        ArgumentTypeRegistry.registerArgumentType(new Identifier(TrulyRandom.MOD_ID, "set_string"), SetStringArgumentType.class, new SetStringArgumentTypeSerializer());

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> CommandHandler.register(dispatcher));
    }
}
