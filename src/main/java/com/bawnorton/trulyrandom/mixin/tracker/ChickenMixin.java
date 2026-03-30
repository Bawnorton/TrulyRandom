package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Mixin(Chicken.class)
abstract class ChickenMixin extends Animal {
    protected ChickenMixin(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @WrapOperation(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/chicken/Chicken;dropFromGiftLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Ljava/util/function/BiConsumer;)Z"
            )
    )
    private boolean trackCause(Chicken instance, ServerLevel level, ResourceKey<?> resourceKey, BiConsumer<?, ?> biConsumer, Operation<Boolean> original) {
        return LootTableTracker.attachAnimalCause(instance, level, () -> original.call(instance, level, resourceKey, biConsumer));
    }
}
