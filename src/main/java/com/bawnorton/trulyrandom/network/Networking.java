package com.bawnorton.trulyrandom.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.listener.ClientPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;

public class Networking {
    private static MinecraftServer server;

    public static void init() {
        ServerTickEvents.START_SERVER_TICK.register(server -> Networking.server = server);
    }

    public static void sendToAllPlayers(Packet<? extends ClientPacketListener> packet) {
        server.getPlayerManager().sendToAll(packet);
    }
}
