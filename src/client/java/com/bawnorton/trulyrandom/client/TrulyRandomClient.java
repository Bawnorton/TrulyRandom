package com.bawnorton.trulyrandom.client;

import com.bawnorton.trulyrandom.client.event.ClientEventHandler;
import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import com.bawnorton.trulyrandom.client.network.ClientNetworking;
import com.bawnorton.trulyrandom.client.random.ClientRandomiser;
import com.mojang.serialization.Codec;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;

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

        Codec.STRING.xmap(name -> Enum.valueOf(BlendMode.class, name), BlendMode::name);
    }
}
