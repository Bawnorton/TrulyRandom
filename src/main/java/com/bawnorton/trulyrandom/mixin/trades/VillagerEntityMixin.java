package com.bawnorton.trulyrandom.mixin.trades;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntityMixin {
    protected VillagerEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(
            method = "fillRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/VillagerEntity;getOffers()Lnet/minecraft/village/TradeOfferList;"
            )
    )
    private TradeOfferList dontUseRandomised(VillagerEntity instance, Operation<TradeOfferList> original) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.TRADES)) return original.call(instance);

        if(offers == null) {
            offers = new TradeOfferList();
            fillRecipes();
        }
        return offers;
    }
}
