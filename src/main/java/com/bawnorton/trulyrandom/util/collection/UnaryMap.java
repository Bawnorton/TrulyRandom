package com.bawnorton.trulyrandom.util.collection;

import java.util.Map;

/**
 * Map where the key and value are the same type.
 */
public interface UnaryMap<K> extends Map<K, K> {
    static <K> UnaryMap<K> of() {
        return new UnaryHashMap<>();
    }
}
