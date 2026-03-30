package com.bawnorton.trulyrandom.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Used to avoid recursion
 */
@MixinEnvironment("client")
@Mixin(BlockBehaviour.class)
public interface AbstractBlockAccessor {
    @Accessor("soundType")
    SoundType trulyrandom$soundType();

    @Accessor("friction")
    float trulyrandom$friction();
}
