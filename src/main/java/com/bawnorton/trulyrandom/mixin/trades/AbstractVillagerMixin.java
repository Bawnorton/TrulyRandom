package com.bawnorton.trulyrandom.mixin.trades;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.mixin.accessor.MerchantOfferAccessor;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.trade.TradeRandomiser;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Optional;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin extends AgeableMob {
    @Shadow
    protected MerchantOffers offers;

    @Shadow
    protected abstract void updateTrades(ServerLevel level);

    protected AbstractVillagerMixin(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(
            method = "getOffers",
            at = @At("RETURN")
    )
    private MerchantOffers randomiseTrades(MerchantOffers original) {
        if(!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.TRADES)) return original;
        if(original.isEmpty()) return original;

        MerchantOffers newOffers = new MerchantOffers();
        TradeRandomiser randomiser = TrulyRandom.getCachedRandomiser().getTradeRandomiser();
        AbstractVillager merchantEntity = (AbstractVillager) (Object) this;
        for(MerchantOffer offer : original) {
            ItemCost firstBuyItem = offer.getItemCostA();
            Item redirected = randomiser.getItem(merchantEntity, firstBuyItem.item().value());
            int count = randomiser.getCount(merchantEntity, redirected, firstBuyItem.count());
            firstBuyItem = new ItemCost(redirected, count);

            Optional<ItemCost> secondBuyItem = offer.getItemCostB();
            if(secondBuyItem.isPresent()) {
                redirected = randomiser.getItem(merchantEntity, secondBuyItem.get().item().value());
                count = randomiser.getCount(merchantEntity, redirected, secondBuyItem.get().count());
                secondBuyItem = Optional.of(new ItemCost(redirected, count));
            }

            ItemStack sellItem = offer.getResult();
            redirected = randomiser.getItem(merchantEntity, sellItem.getItem());
            count = randomiser.getCount(merchantEntity, redirected, sellItem.getCount());
            sellItem = sellItem.transmuteCopy(redirected);
            sellItem.setCount(count);

            newOffers.add(MerchantOfferAccessor.trulyrandom$init(
                    firstBuyItem,
                    secondBuyItem,
                    sellItem,
                    offer.getUses(),
                    offer.getMaxUses(),
                    offer.shouldRewardExp(),
                    offer.getSpecialPriceDiff(),
                    offer.getDemand(),
                    offer.getPriceMultiplier(),
                    offer.getXp()
            ));
        }

        return newOffers;
    }

    @WrapOperation(
            method = "addAdditionalSaveData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/npc/villager/AbstractVillager;getOffers()Lnet/minecraft/world/item/trading/MerchantOffers;"
            )
    )
    private MerchantOffers dontWriteRandomised(AbstractVillager instance, Operation<MerchantOffers> original) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.TRADES)) return original.call(instance);

        if(offers == null && level() instanceof ServerLevel level) {
            offers = new MerchantOffers();
            updateTrades(level);
        }
        return offers;
    }
}
