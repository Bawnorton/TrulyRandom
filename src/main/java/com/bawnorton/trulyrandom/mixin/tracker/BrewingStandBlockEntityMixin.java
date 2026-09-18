package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.extend.BrewingStandBlockEntityExtender;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(BrewingStandBlockEntity.class)
abstract class BrewingStandBlockEntityMixin implements BrewingStandBlockEntityExtender {
    @Unique
    private final Map<Integer, RecipeHolder<?>> trulyrandom$RECIPES = new HashMap<>();

    @WrapOperation(
            method = "doBrew",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 0
            )
    )
    private static <E> E recordRecipes(NonNullList<E> instance, int index, E element, Operation<E> original, @Local Optional<RecipeHolder<BrewingRecipe>> recipe, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) BrewingStandBlockEntity entity) {
        recipe.ifPresent(holder -> ((BrewingStandBlockEntityExtender) entity).trulyrandom$RECIPES().put(index, holder));
        return original.call(instance, index, element);
    }

    @Override
    public Map<Integer, RecipeHolder<?>> trulyrandom$RECIPES() {
        return trulyrandom$RECIPES;
    }
}
