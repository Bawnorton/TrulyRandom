package com.bawnorton.trulyrandom.client.event;

import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import com.bawnorton.trulyrandom.network.packet.c2s.HandshakeC2SPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ClientEventHandler {
    public static void init() {
        registerKeybindEvents();
        registerWorldJoinEvent();
    }

    private static void registerKeybindEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(KeybindManager::runKeybindActions);
    }

    private static void registerWorldJoinEvent() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> sender.sendPacket(new HandshakeC2SPacket()));
    }
}
