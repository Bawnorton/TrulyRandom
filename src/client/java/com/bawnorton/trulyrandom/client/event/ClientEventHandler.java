package com.bawnorton.trulyrandom.client.event;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import com.bawnorton.trulyrandom.client.screen.TrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.network.packet.c2s.HandshakeC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.SyncRandomiserC2SPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.item.Item;

public class ClientEventHandler {
    public static void init() {
        registerKeybindEvents();
        registerWorldJoinEvent();
    }

    private static void registerKeybindEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (KeybindManager.OPEN_RANDOMISER_GUI.wasPressed()) {
                client.setScreen(new TrulyRandomSettingsScreen(client.currentScreen, (modules, seed) -> client.getNetworkHandler().sendPacket(new SyncRandomiserC2SPacket(modules, seed))));
            }
            KeybindManager.RELOAD_CHUNKS.ifPresent(keybind -> {
                while (keybind.wasPressed()) {
                    client.worldRenderer.reload();
                }
            });
            KeybindManager.QUERY_HAND.ifPresent(keyBinding -> {
                while (keyBinding.wasPressed()) {
                    Item handItem = client.player.getMainHandStack().getItem();
                    ModelShuffler.Items items = (ModelShuffler.Items) client.getItemRenderer().getModels();
                    System.out.println("Hand item: " + handItem + " (" + items.trulyrandom$getOriginalRandomisedMap().get(handItem) + ")");
                }
            });
        });
    }

    private static void registerWorldJoinEvent() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            sender.sendPacket(new HandshakeC2SPacket());
        });
    }
}
