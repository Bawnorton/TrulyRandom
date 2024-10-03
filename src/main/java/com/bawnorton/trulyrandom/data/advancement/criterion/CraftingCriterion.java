package com.bawnorton.trulyrandom.data.advancement.criterion;

import com.bawnorton.trulyrandom.registry.TrulyRandomCriteria;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CraftingCriterion extends AbstractCriterion<CraftingCriterion.Conditions> {
    @Override
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, CraftingRecipeInput input) {
        trigger(player, conditions -> conditions.recipe().matches(input));
    }

    public record Conditions(Optional<LootContextPredicate> player, RawShapedRecipe recipe) implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                        RawShapedRecipe.CODEC.forGetter(Conditions::recipe)
                ).apply(instance, Conditions::new)
        );

        public static AdvancementCriterion<Conditions> create(List<String> pattern, Map<Character, Ingredient> inputs) {
            return TrulyRandomCriteria.CRAFTING.create(new Conditions(Optional.empty(), RawShapedRecipe.create(inputs, pattern)));
        }
    }
}
