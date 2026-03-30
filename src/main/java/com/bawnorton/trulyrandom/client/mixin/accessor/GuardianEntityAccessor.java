package com.bawnorton.trulyrandom.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.entity.monster.Guardian;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment("client")
@Mixin(Guardian.class)
public interface GuardianEntityAccessor {
    @Accessor("clientSideTailAnimation")
    void trulyrandom$clientSideTailAnimation(float angle);
}
