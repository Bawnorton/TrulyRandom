package com.bawnorton.trulyrandom.data.advancement.criterion;

import com.bawnorton.trulyrandom.registry.TrulyRandomCriteria;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//? if <=26.1.2 {
/*import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
*///?} else {
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
//?}

public final class CraftingCriterion extends SimpleCriterionTrigger<CraftingCriterion.Conditions> {
    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, CraftingInput input) {
        trigger(player, conditions -> conditions.recipe().matches(input));
    }

    //~ if <=26.1.2 'Holder<LootItemCondition>' -> 'ContextAwarePredicate'
    public record Conditions(Optional<Holder<LootItemCondition>> player, ShapedRecipePattern recipe) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        //~ if <=26.1.2 'LootItemCondition.CODEC' -> 'EntityPredicate.ADVANCEMENT_CODEC'
                        LootItemCondition.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                        ShapedRecipePattern.MAP_CODEC.forGetter(Conditions::recipe)
                ).apply(instance, Conditions::new)
        );

        public static Criterion<Conditions> create(List<String> pattern, Map<Character, Ingredient> inputs) {
            return TrulyRandomCriteria.CRAFTING.createCriterion(new Conditions(Optional.empty(), ShapedRecipePattern.of(inputs, pattern)));
        }
    }
}
