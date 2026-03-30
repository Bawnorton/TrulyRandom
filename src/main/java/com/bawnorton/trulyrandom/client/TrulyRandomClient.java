package com.bawnorton.trulyrandom.client;

import com.bawnorton.trulyrandom.client.event.ClientEventHandler;
import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import com.bawnorton.trulyrandom.client.graph.TrackingGraphBookController;
import com.bawnorton.trulyrandom.client.network.ClientNetworking;
import com.bawnorton.trulyrandom.client.random.ClientRandomiser;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.api.ClientModInitializer;

@Entrypoint("client")
public class TrulyRandomClient implements ClientModInitializer {
    private static final ClientRandomiser randomiser = ClientRandomiser.DEFAULT;
    private static final TrackingGraphBookController TRACKING_GRAPH_BOOK_CONTROLLER = new TrackingGraphBookController();

    public static ClientRandomiser getRandomiser() {
        return randomiser;
    }

    public static TrackingGraphBookController getLootBookController() {
        return TRACKING_GRAPH_BOOK_CONTROLLER;
    }

    @Override
    public void onInitializeClient() {
        ClientNetworking.init();
        ClientEventHandler.init();
        KeybindManager.init();
    }
}