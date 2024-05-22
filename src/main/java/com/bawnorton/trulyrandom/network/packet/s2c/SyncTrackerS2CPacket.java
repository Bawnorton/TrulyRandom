package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.tracker.Tracker;
import net.minecraft.network.packet.CustomPayload;

public abstract class SyncTrackerS2CPacket<T extends Tracker<?, ?>> implements CustomPayload {
    protected final T tracker;

    public SyncTrackerS2CPacket(T tracker) {
        this.tracker = tracker;
    }

    public T tracker() {
        return tracker;
    }
}
