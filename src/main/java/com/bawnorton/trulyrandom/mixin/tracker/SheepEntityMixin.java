package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends AnimalEntity {
    @Shadow public abstract RegistryKey<LootTable> getLootTableId();

    @Shadow public abstract void setSheared(boolean sheared);

    protected SheepEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(
            method = "sheared",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I"
            )
    )
    private int replaceWithLootTable(Random instance, int i, Operation<Integer> original) {
        int result = original.call(instance, i);
        if(getWorld().isClient()) return result;
        if(!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return result;

        setSheared(false);
        int amount = 1 + result;
        RegistryKey<LootTable> lootTable = getLootTableId();
        ServerWorld serverWorld = (ServerWorld) this.getWorld();
        MinecraftServer server = serverWorld.getServer();
        LootTable table = server.getReloadableRegistries().getLootTable(lootTable);
        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(serverWorld)
                .add(LootContextParameters.THIS_ENTITY, this)
                .add(LootContextParameters.ORIGIN, this.getPos());
        for(int j = 0; j < amount; ++j) {
            List<ItemStack> loot = table.generateLoot(builder.build(LootContextTypes.SHEARING));
            for(ItemStack stack : loot) {
                ItemEntity itemEntity = dropStack(stack, 1);
                if (itemEntity == null) continue;

                itemEntity.setVelocity(itemEntity.getVelocity()
                        .add(
                                (random.nextFloat() - random.nextFloat()) * 0.1F,
                                random.nextFloat() * 0.05F,
                                (random.nextFloat() - random.nextFloat()) * 0.1F
                        )
                );
            }
        }
        setSheared(true);
        return -1;
    }
}
