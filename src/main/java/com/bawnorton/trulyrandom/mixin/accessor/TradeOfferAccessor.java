package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.Optional;

@Mixin(TradeOffer.class)
public interface TradeOfferAccessor {
    @Invoker("<init>")
    static TradeOffer callInit(
            TradedItem firstBuyItem,
            Optional<TradedItem> secondBuyItem,
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
