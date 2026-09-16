package com.bawnorton.trulyrandom.mixin.server;

import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.random.module.Modules;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.server.WorldStem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@MixinEnvironment(type = MixinEnvironment.Env.SERVER)
@Mixin(WorldStem.class)
abstract class WorldStemMixin implements ModulesHolder {
    @Unique
    private Modules trulyrandom$modules;

    public Modules trulyrandom$getRandomiserModules() {
        return trulyrandom$modules;
    }

    public void trulyrandom$setRandomiserModules(Modules modules) {
        this.trulyrandom$modules = modules;
    }
}
