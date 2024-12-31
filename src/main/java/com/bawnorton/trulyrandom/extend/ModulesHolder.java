package com.bawnorton.trulyrandom.extend;

import com.bawnorton.trulyrandom.random.module.Modules;

public interface ModulesHolder {
    Modules trulyrandom$getRandomiserModules();
    void trulyrandom$setRandomiserModules(Modules modules);
}
