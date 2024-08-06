package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.tag.EnchantmentTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {
    @Inject(
            method = "getDroppedStacks",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/registry/ReloadableRegistries$Lookup;getLootTable(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/loot/LootTable;"
            )
    )
    private void trackCause(BlockState state, LootContextParameterSet.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        
        LootTableTracker.LOOT_CAUSERS.remove();
        Entity entity = builder.getOptional(LootContextParameters.THIS_ENTITY);
        if(entity instanceof PlayerEntity player) {
            LootTableTracker.BROKEN_WITH_SILK.set(EnchantmentHelper.hasAnyEnchantmentsIn(player.getMainHandStack(), EnchantmentTags.PREVENTS_INFESTED_SPAWNS));
            LootTableTracker.LOOT_CAUSERS.set(List.of(player.trulyrandom$getTeam()));
        }
    }
}
