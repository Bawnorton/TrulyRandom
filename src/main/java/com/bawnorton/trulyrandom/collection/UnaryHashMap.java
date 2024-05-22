package com.bawnorton.trulyrandom.collection;

import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
