package com.bawnorton.trulyrandom.client.extend;

import com.bawnorton.trulyrandom.random.module.Modules;

public interface DataConfigurationExtender {
    Modules trulyrandom$getRandomiserModules();
    void trulyrandom$setRandomiserModules(Modules modules);
}
