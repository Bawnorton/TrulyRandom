package com.bawnorton.trulyrandom.mixin.recipe;

import com.bawnorton.trulyrandom.extend.ResultClearer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({
        DyeRecipe.class,
        BannerDuplicateRecipe.class,
        BookCloningRecipe.class,
        DecoratedPotRecipe.class,
        FireworkRocketRecipe.class,
        FireworkStarFadeRecipe.class,
        FireworkStarRecipe.class,
//        MapCloningRecipe.class,
        MapExtendingRecipe.class,
        RepairItemRecipe.class,
        ShieldDecorationRecipe.class,
        ImbueRecipe.class,
        SmithingTrimRecipe.class,
        TransmuteRecipe.class
})
abstract class SpecialRecipeMixin implements ResultClearer {
    @Unique
    private ItemStack result = ItemStack.EMPTY;

    @ModifyReturnValue(
            method = "assemble(Lnet/minecraft/world/item/crafting/RecipeInput;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN")
    )
    private ItemStack useRandomResult(ItemStack result) {
        if (this.result.isEmpty()) return result;
        return this.result;
    }

    @Override
    public void trulyrandom$setResult(ItemStack result) {
        this.result = result;
    }

    @Override
    public ItemStack trulyrandom$getResult() {
        return result;
    }
}
