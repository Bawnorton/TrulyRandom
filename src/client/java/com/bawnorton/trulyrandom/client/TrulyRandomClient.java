package com.bawnorton.trulyrandom.client;

import com.bawnorton.trulyrandom.client.event.ClientEventHandler;
import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import net.fabricmc.api.ClientModInitializer;

public class TrulyRandomClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEventHandler.init();
        KeybindManager.init();
    }
}
