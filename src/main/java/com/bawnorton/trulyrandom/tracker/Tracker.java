package com.bawnorton.trulyrandom.tracker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.UUID;

public abstract class Tracker<F, T> {
    private boolean dirty = false;
    protected @Nullable UUID playerId;

    protected Tracker(@Nullable UUID playerId) {
        this.playerId = playerId;
    }

    public void setPlayerId(@NotNull UUID playerId) {
        this.playerId = playerId;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDirty() {
        dirty = true;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public abstract void track(F from, T to);

    public void copy(Tracker<F, T> other) {
        reset();
        other.known().forEach(entry -> track(entry.getKey(), entry.getValue()));
    }

    public abstract Iterable<Map.Entry<F, T>> known();

    public abstract void reset();
}
