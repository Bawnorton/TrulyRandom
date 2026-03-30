package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Mixin(Armadillo.class)
abstract class ArmadilloMixin extends Animal {
    protected ArmadilloMixin(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @WrapOperation(
            method = "customServerAiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/armadillo/Armadillo;dropFromGiftLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Ljava/util/function/BiConsumer;)Z"
            )
    )
    private boolean trackCause(Armadillo instance, ServerLevel level, ResourceKey<?> resourceKey, BiConsumer<?, ?> biConsumer, Operation<Boolean> original) {
        return LootTableTracker.attachAnimalCause(instance, level, () -> original.call(instance, level, resourceKey, biConsumer));
    }

    @WrapOperation(
            method = "brushOffScute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/armadillo/Armadillo;dropFromEntityInteractLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemInstance;Ljava/util/function/BiConsumer;)Z"
            )
    )
    private boolean trackCause(Armadillo instance, ServerLevel level, ResourceKey<?> resourceKey, Entity entity, ItemInstance itemInstance, BiConsumer<?, ?> biConsumer, Operation<Boolean> original) {
        return LootTableTracker.attachCauser(entity, () -> original.call(instance, level, resourceKey, entity, itemInstance, biConsumer));
    }
}
