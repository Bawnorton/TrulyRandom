package com.bawnorton.trulyrandom.data.advancement.criterion;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.registry.TrulyRandomCriteria;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Optional;

public final class ModuleEnabledCriterion extends AbstractCriterion<ModuleEnabledCriterion.Conditions> {
    @Override
    public Codec<ModuleEnabledCriterion.Conditions> getConditionsCodec() {
        return ModuleEnabledCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player) {
        trigger(player, conditions -> TrulyRandom.getRandomiser(player.getServer()).getModules().isEnabled(conditions.module));
    }

    public record Conditions(Optional<LootContextPredicate> player, Module module) implements AbstractCriterion.Conditions {
        public static final Codec<ModuleEnabledCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ModuleEnabledCriterion.Conditions::player),
                        Module.CODEC.fieldOf("module").forGetter(ModuleEnabledCriterion.Conditions::module)
                ).apply(instance, ModuleEnabledCriterion.Conditions::new)
        );

        public static AdvancementCriterion<ModuleEnabledCriterion.Conditions> create(Module module) {
            return TrulyRandomCriteria.MODULE_ENABLED.create(new ModuleEnabledCriterion.Conditions(Optional.empty(), module));
        }
    }
}