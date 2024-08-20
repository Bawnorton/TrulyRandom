package com.bawnorton.trulyrandom.util.collection;

import java.util.HashMap;
import java.util.Map;

public class UnaryHashMap<K> extends HashMap<K, K> implements UnaryMap<K> {
    public UnaryHashMap(Map<K, K> map) {
        super(map);
    }

    public UnaryHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public UnaryHashMap() {
        super();
    }
}
