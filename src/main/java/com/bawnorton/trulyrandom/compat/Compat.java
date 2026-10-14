package com.bawnorton.trulyrandom.compat;

import com.bawnorton.trulyrandom.compat.rrv.RRVCompat;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Optional;

public class Compat {
    private RRVCompat rrvCompat;

    public Optional<RRVCompat> getRRVCompat() {
        if (!FabricLoader.getInstance().isModLoaded("rrv")) return Optional.empty();
        if (rrvCompat == null) {
            rrvCompat = new RRVCompat();
        }
        return Optional.of(rrvCompat);
    }
}
