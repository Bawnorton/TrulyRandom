package com.bawnorton.trulyrandom.network.packet.clientbound;

import com.bawnorton.trulyrandom.tracker.Tracker;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class ClientboundSyncTrackerPacket<T extends Tracker<?, ?>> implements CustomPacketPayload {
    protected final T tracker;

    public ClientboundSyncTrackerPacket(T tracker) {
        this.tracker = tracker;
    }

    public T tracker() {
        return tracker;
    }
}
