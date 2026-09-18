package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.extend.BrewingStandBlockEntityExtender;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.List;
import java.util.Map;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
abstract class BrewingStandMenu$PotionSlotMixin extends Slot {
    protected BrewingStandMenu$PotionSlotMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @WrapOperation(
            method = "onTake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/Slot;onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"
            )
    )
    private void triggerCrafted(@Coerce Slot instance, Player player, ItemStack carried, Operation<Void> original) {
        if(instance.container instanceof BrewingStandBlockEntityExtender extender) {
            Map<Integer, RecipeHolder<?>> recipes = extender.trulyrandom$RECIPES();
            if(recipes.containsKey(this.index)) {
                RecipeHolder<?> holder = recipes.get(this.index);
                player.triggerRecipeCrafted(holder, List.of(carried));
            }
        }
        original.call(instance, player, carried);
    }
}
