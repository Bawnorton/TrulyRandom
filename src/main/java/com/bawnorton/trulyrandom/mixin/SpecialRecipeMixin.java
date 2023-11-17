package com.bawnorton.trulyrandom.mixin;

import com.bawnorton.trulyrandom.extend.ResultClearer;
import com.bawnorton.trulyrandom.extend.ResultSetter;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({
        ArmorDyeRecipe.class,
        BannerDuplicateRecipe.class,
        BookCloningRecipe.class,
        CraftingDecoratedPotRecipe.class,
        FireworkRocketRecipe.class,
        FireworkStarFadeRecipe.class,
        FireworkStarRecipe.class,
        MapCloningRecipe.class,
        MapExtendingRecipe.class,
        RepairItemRecipe.class,
        ShieldDecorationRecipe.class,
        ShulkerBoxColoringRecipe.class,
        SuspiciousStewRecipe.class,
        TippedArrowRecipe.class,
        SmithingTrimRecipe.class
})
public abstract class SpecialRecipeMixin implements ResultClearer {
    @Unique
    private ItemStack result = ItemStack.EMPTY;

    @ModifyReturnValue(method = "craft(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"))
    private ItemStack useRandomResult(ItemStack result) {
        if(this.result.isEmpty()) return result;
        return this.result;
    }

    @Override
    public void trulyRandom$setResult(ItemStack result) {
        this.result = result;
    }
}
