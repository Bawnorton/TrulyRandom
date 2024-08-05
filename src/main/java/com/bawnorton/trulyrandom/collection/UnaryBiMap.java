package com.bawnorton.trulyrandom.collection;

import com.google.common.collect.BiMap;

/**
 * Map where the key and value are the same type.
 */
public interface UnaryBiMap<K> extends BiMap<K, K> {
}
