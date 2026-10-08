package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.DyeRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DyeRecipe.class)
public interface DyeRecipeAccessor {
    @Accessor("result")
    ItemStackTemplate trulyrandom$result();
}
