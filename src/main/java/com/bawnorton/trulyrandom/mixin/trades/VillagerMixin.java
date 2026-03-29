package com.bawnorton.trulyrandom.mixin.trades;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Villager.class)
abstract class VillagerMixin extends AbstractVillagerMixin {
    protected VillagerMixin(EntityType<? extends AgeableMob> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "updateTrades",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/npc/villager/Villager;getOffers()Lnet/minecraft/world/item/trading/MerchantOffers;"
            )
    )
    private MerchantOffers dontUseRandomised(Villager instance, Operation<MerchantOffers> original) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.TRADES)) return original.call(instance);

        if(offers == null && level() instanceof ServerLevel level) {
            offers = new MerchantOffers();
            updateTrades(level);
        }
        return offers;
    }
}
