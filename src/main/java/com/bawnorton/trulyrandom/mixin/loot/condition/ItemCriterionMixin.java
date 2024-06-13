package com.bawnorton.trulyrandom.mixin.loot.condition;

import com.bawnorton.trulyrandom.extend.ItemCriterionCapture;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCriterion.class)
public abstract class ItemCriterionMixin {
    @Inject(method = "trigger", at = @At("HEAD"))
    private void capturedTriggeredCriterion(ServerPlayerEntity player, BlockPos pos, ItemStack stack, CallbackInfo ci) {
        ItemCriterionCapture.TRIGGERD_CRITERION.set((ItemCriterion) (Object) this);
    }
}
