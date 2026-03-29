package com.bawnorton.trulyrandom.client.util;

import net.minecraft.client.Minecraft;

import java.util.List;

public final class Cycler {
    private static int counter;
    private static int offset;

    public static <T> T one(List<T> list) {
        if(list.isEmpty()) return null;

        counter++;
        if(counter >= Minecraft.getInstance().getFps()) {
            counter = 0;
            offset++;
            if(offset >= 1000000) {
                offset = 0;
            }
        }
        return list.get(offset % list.size());
    }
}
