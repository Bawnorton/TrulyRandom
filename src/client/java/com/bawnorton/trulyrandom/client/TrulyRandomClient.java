package com.bawnorton.trulyrandom.client;

import com.bawnorton.trulyrandom.client.event.ClientEventHandler;
import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import com.bawnorton.trulyrandom.client.network.ClientNetworking;
import com.bawnorton.trulyrandom.client.random.ClientRandomiser;
import net.fabricmc.api.ClientModInitializer;

public class TrulyRandomClient implements ClientModInitializer {
    private static final ClientRandomiser randomiser = ClientRandomiser.DEFAULT;

    public static ClientRandomiser getRandomiser() {
        return randomiser;
    }

    @Override
    public void onInitializeClient() {
        ClientNetworking.init();
        ClientEventHandler.init();
        KeybindManager.init();
    }
}
