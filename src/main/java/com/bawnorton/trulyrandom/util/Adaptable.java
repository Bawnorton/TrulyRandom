package com.bawnorton.trulyrandom.util;

import org.apache.commons.lang3.Validate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Adaptable<K, T> {
    void setDefaultAdapter(T adapter);

    T getDefaultAdapter();

    Map<K, T> getAdapterMap();

    default void registerAdapters(Set<K> keys, T adpater) {
        keys.forEach(key -> getAdapterMap().put(key, adpater));
    }

    default void registerAdapter(K key, T adpater) {
        getAdapterMap().put(key, adpater);
    }

    default T getAdapter(K key) {
        Validate.notNull(key, "key cannot be null");
        return getAdapterMap().computeIfAbsent(key, k -> {
            T defaultAdapter = getDefaultAdapter();
            Validate.notNull(defaultAdapter, "Adapter for \"%s\" is not registered and no default adapter set".formatted(key));
            return defaultAdapter;
        });
    }

    default List<T> getAdapters() {
        List<T> adapters = new ArrayList<>(getAdapterMap().values());
        adapters.add(getDefaultAdapter());
        return adapters;
    }
}
