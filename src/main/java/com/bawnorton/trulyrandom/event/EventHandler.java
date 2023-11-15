package com.bawnorton.trulyrandom.event;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.Randomiser;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class EventHandler {
    public static void init() {
        registerServerEvents();
    }

    private static void registerServerEvents() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            Randomiser randomiser = TrulyRandom.getRandomiser(server);
            randomiser.updateLoot(server);
        });
    }
}
