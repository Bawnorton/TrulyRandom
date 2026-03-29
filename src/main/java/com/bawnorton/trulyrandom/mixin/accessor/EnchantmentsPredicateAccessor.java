package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.List;

@Mixin(EnchantmentsPredicate.class)
public interface EnchantmentsPredicateAccessor {
    @Invoker("enchantments")
    List<EnchantmentPredicate> trulyrandom$enchantments();
}
