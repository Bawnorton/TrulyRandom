package com.bawnorton.trulyrandom.data.advancement.criterion;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.registry.TrulyRandomCriteria;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class ModuleEnabledCriterion extends SimpleCriterionTrigger<ModuleEnabledCriterion.Conditions> {

    @Override
    public Codec<ModuleEnabledCriterion.Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player) {
        trigger(player, conditions -> TrulyRandom.getRandomiser(player.level().getServer()).getModules().isEnabled(conditions.module));
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Module module) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<ModuleEnabledCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ModuleEnabledCriterion.Conditions::player),
                        Module.CODEC.fieldOf("module").forGetter(ModuleEnabledCriterion.Conditions::module)
                ).apply(instance, ModuleEnabledCriterion.Conditions::new)
        );

        public static Criterion<Conditions> create(Module module) {
            return TrulyRandomCriteria.MODULE_ENABLED.createCriterion(new ModuleEnabledCriterion.Conditions(Optional.empty(), module));
        }
    }
}