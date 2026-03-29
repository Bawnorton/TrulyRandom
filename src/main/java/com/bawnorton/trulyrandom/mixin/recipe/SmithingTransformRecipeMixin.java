package com.bawnorton.trulyrandom.mixin.recipe;

import com.bawnorton.trulyrandom.extend.ResultHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SmithingTransformRecipe.class)
abstract class SmithingTransformRecipeMixin implements ResultHolder {
    @Mutable
    @Final
    @Shadow
    private ItemStackTemplate result;

    @Override
    public void trulyrandom$setResult(ItemStack result) {
        this.result = new ItemStackTemplate(
                BuiltInRegistries.ITEM.wrapAsHolder(result.getItem()),
                result.getCount(),
                result.getComponentsPatch()
        );
    }

    @Override
    public ItemStack trulyrandom$getResult() {
        Item item = result.item().value();
        ItemStack stack = new ItemStack(item, result.count());
        stack.applyComponentsAndValidate(result.components());
        return stack;
    }
}
