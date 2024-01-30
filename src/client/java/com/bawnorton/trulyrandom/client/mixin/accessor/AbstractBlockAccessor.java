package com.bawnorton.trulyrandom.client.mixin.accessor;

import net.minecraft.block.AbstractBlock;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Used to avoid recursion
 */
@Mixin(AbstractBlock.class)
public interface AbstractBlockAccessor {
    @Accessor("soundGroup")
    BlockSoundGroup trulyrandom$getSoundGroup();

    @Accessor("slipperiness")
    float trulyrandom$getSlipperiness();
}
