package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(MerchantOffer.class)
public interface MerchantOfferAccessor {
    @Invoker("<init>")
    static MerchantOffer trulyrandom$init(
            ItemCost firstBuyItem,
            Optional<ItemCost> secondBuyItem,
            ItemStack sellItem,
            int uses,
            int maxUses,
            boolean rewardingPlayerExperience,
            int specialPrice,
            int demandBonus,
            float priceMultiplier,
            int merchantExperience
    ) {
        throw new AssertionError();
    }
}
