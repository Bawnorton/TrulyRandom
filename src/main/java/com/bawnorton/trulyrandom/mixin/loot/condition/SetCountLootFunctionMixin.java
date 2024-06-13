package com.bawnorton.trulyrandom.mixin.loot.condition;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.SetCountLootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SetCountLootFunction.class)
public abstract class SetCountLootFunctionMixin {
    @ModifyReturnValue(method = "process", at = @At("RETURN"))
    private ItemStack ensureAtLeastOne(ItemStack stack) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return stack;
        if (stack.getCount() != 0) return stack;

        stack.setCount(1);
        return stack;
    }
}
