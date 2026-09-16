package com.bawnorton.trulyrandom.client.util;

import java.util.List;

public final class Cycler<T> {
    private final List<T> list;
    private long target = 0;
    private int offset;
    public Cycler(List<T> list) {
        this.list = list;
    }

    public T current() {
        if (list.isEmpty()) return null;
        if (list.size() == 1) return list.getFirst();

        if (System.currentTimeMillis() >= target) {
            offset++;
            if (offset >= list.size()) {
                offset = 0;
            }
            target = System.currentTimeMillis() + 1000;
        }

        return list.get(offset % list.size());
    }
}
