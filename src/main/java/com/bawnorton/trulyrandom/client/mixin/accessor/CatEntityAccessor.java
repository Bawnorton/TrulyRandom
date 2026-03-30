package com.bawnorton.trulyrandom.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment("client")
@Mixin(Cat.class)
public interface CatEntityAccessor {
    @Accessor("lieDownAmount")
    void trulyrandom$lieDownAmount(float amount);
}
