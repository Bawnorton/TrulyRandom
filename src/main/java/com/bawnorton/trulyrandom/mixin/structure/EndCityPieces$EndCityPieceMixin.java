package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.levelgen.structure.structures.EndCityPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EndCityPieces.EndCityPiece.class)
abstract class EndCityPieces$EndCityPieceMixin {
  @ModifyArg(
          method = "handleDataMarker",
          at = @At(
                  value = "INVOKE",
                  target = "Lnet/minecraft/world/entity/decoration/ItemFrame;setItem(Lnet/minecraft/world/item/ItemStack;Z)V"
          )
  )
  private ItemStack LootrHandleDataMarker(ItemStack itemStack) {
    if (!RandomiserSaveLoader.getWorldGenModules().isEnabled(Module.STRUCTURES)) return itemStack;

    ItemStack stack = Items.ELYTRA.getDefaultInstance();
    stack.set(DataComponents.LORE, ItemLore.EMPTY.withLineAdded(Component.literal("You can repair this right? :clueless:")));
    stack.setDamageValue(stack.getMaxDamage() - 1);
    return stack;
  }
}