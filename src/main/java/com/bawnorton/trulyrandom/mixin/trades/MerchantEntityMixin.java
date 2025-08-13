package com.bawnorton.trulyrandom.mixin.trades;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.mixin.accessor.TradeOfferAccessor;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.trade.TradeRandomiser;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin extends PassiveEntity {
    @Shadow @Nullable protected TradeOfferList offers;

    @Shadow protected abstract void fillRecipes();

    protected MerchantEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyReturnValue(
            method = "getOffers",
            at = @At("RETURN")
    )
    private TradeOfferList randomiseTrades(TradeOfferList original) {
        if(!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.TRADES)) return original;
        if(original.isEmpty()) return original;

        TradeOfferList newOffers = new TradeOfferList();
        TradeRandomiser randomiser = TrulyRandom.getCachedRandomiser().getTradeRandomiser();
        MerchantEntity merchantEntity = (MerchantEntity) (Object) this;
        for(TradeOffer offer : original) {
            TradedItem firstBuyItem = offer.getFirstBuyItem();
            Item redirected = randomiser.getItem(merchantEntity, firstBuyItem.item().value());
            int count = randomiser.getCount(merchantEntity, redirected, firstBuyItem.count());
            firstBuyItem = new TradedItem(redirected, count);

            Optional<TradedItem> secondBuyItem = offer.getSecondBuyItem();
            if(secondBuyItem.isPresent()) {
                redirected = randomiser.getItem(merchantEntity, secondBuyItem.get().item().value());
                count = randomiser.getCount(merchantEntity, redirected, secondBuyItem.get().count());
                secondBuyItem = Optional.of(new TradedItem(redirected, count));
            }

            ItemStack sellItem = offer.getSellItem();
            redirected = randomiser.getItem(merchantEntity, sellItem.getItem());
            count = randomiser.getCount(merchantEntity, redirected, sellItem.getCount());
            sellItem = sellItem.withItem(redirected);
            sellItem.setCount(count);

            newOffers.add(TradeOfferAccessor.callInit(
                    firstBuyItem,
                    secondBuyItem,
                    sellItem,
                    offer.getUses(),
                    offer.getMaxUses(),
                    offer.shouldRewardPlayerExperience(),
                    offer.getSpecialPrice(),
                    offer.getDemandBonus(),
                    offer.getPriceMultiplier(),
                    offer.getMerchantExperience()
            ));
        }

        return newOffers;
    }

    @WrapOperation(
            method = "writeCustomData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/MerchantEntity;getOffers()Lnet/minecraft/village/TradeOfferList;"
            )
    )
    private TradeOfferList dontWriteRandomised(MerchantEntity instance, Operation<TradeOfferList> original) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.TRADES)) return original.call(instance);

        if(offers == null) {
            offers = new TradeOfferList();
            fillRecipes();
        }
        return offers;
    }
}
