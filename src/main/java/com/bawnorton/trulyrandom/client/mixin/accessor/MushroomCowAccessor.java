package com.bawnorton.trulyrandom.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@MixinEnvironment("client")
@Mixin(MushroomCow.class)
public interface MushroomCowAccessor {
    @Invoker("setVariant")
    void trulyrandom$setVariant(MushroomCow.Variant variant);
}
