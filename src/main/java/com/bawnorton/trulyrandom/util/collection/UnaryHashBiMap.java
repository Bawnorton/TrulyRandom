package com.bawnorton.trulyrandom.util.collection;

import com.google.common.collect.BiMap;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.HashBiMap;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Set;

public class UnaryHashBiMap<K> extends ForwardingMap<K, K> implements UnaryBiMap<K> {
    private final BiMap<K, K> delegate;

    public UnaryHashBiMap() {
        this.delegate = HashBiMap.create();
    }

    public UnaryHashBiMap(Map<K, K> map) {
        this.delegate = HashBiMap.create(map);
    }

    public UnaryHashBiMap(int size) {
        this.delegate = HashBiMap.create(size);
    }

    @Override
    protected @NotNull Map<K, K> delegate() {
        return delegate;
    }

    @Override
    public @NotNull Set<K> values() {
        return delegate.values();
    }

    @Override
    public K forcePut(K key, K value) {
        return delegate.forcePut(key, value);
    }

    @Override
    public @NotNull BiMap<K, K> inverse() {
        return delegate.inverse();
    }
}
