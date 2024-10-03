package com.bawnorton.trulyrandom.client.extend;

import com.bawnorton.trulyrandom.random.module.Modules;

public interface ModulesHolder {
    Modules trulyrandom$getRandomiserModules();
    void trulyrandom$setRandomiserModules(Modules modules);
}
