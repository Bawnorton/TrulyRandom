package com.bawnorton.trulyrandom.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.resources.model.ModelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment("client")
@Mixin(ModelManager.class)
public interface ModelManagerAccessor {
    @Accessor("blockStateModelSet")
    BlockStateModelSet trulyrandom$blockStateModelSet();
}
