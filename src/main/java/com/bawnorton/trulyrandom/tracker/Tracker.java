package com.bawnorton.trulyrandom.tracker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public abstract class Tracker<F, T> {
    private boolean dirty = false;
    protected Team team;

    protected Tracker(Team team) {
        this.team = team;
    }

    public void setTeam(@NotNull Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
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

    public abstract Map<F, T> known();

    public abstract void reset();
}
