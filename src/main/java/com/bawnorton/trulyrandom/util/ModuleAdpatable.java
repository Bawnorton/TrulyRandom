package com.bawnorton.trulyrandom.util;

import com.bawnorton.trulyrandom.random.module.Module;
import java.util.HashMap;
import java.util.Map;

public abstract class ModuleAdpatable<T> implements Adaptable<Module, T> {
    private final Map<Module, T> adapters = new HashMap<>();
    private T defaultAdpater;

    @Override
    public void setDefaultAdapter(T adapter) {
        this.defaultAdpater = adapter;
    }

    @Override
    public T getDefaultAdapter() {
        return defaultAdpater;
    }

    @Override
    public Map<Module, T> getAdapterMap() {
        return this.adapters;
    }
}
