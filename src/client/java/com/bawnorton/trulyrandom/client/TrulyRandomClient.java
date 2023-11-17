package com.bawnorton.trulyrandom.client;

import com.bawnorton.trulyrandom.client.event.ClientEventHandler;
import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import com.bawnorton.trulyrandom.client.random.ClientRandomiser;
import net.fabricmc.api.ClientModInitializer;

public class TrulyRandomClient implements ClientModInitializer {
    private static final ClientRandomiser randomiser = new ClientRandomiser();

    public static ClientRandomiser getRandomiser() {
        return randomiser;
    }

    @Override
    public void onInitializeClient() {
        ClientEventHandler.init();
        KeybindManager.init();
    }
}
