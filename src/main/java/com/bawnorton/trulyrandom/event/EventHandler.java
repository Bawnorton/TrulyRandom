package com.bawnorton.trulyrandom.event;

import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class EventHandler {
    public static void init() {
        registerWorldLoadEvents();
    }

    private static void registerWorldLoadEvents() {
        ServerWorldEvents.LOAD.register((server, world) -> {
//            RandomiserSaveLoader.getServerState(server);
//            RandomiserSaveLoader.clearDefaultRandomiser();
        });
    }
}
