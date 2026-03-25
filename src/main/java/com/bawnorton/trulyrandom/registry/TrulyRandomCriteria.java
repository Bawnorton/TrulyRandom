package com.bawnorton.trulyrandom.registry;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.data.advancement.criterion.CraftingCriterion;
import com.bawnorton.trulyrandom.data.advancement.criterion.ModuleEnabledCriterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class TrulyRandomCriteria {
    public static final CraftingCriterion CRAFTING = register("crafting", new CraftingCriterion());
    public static final ModuleEnabledCriterion MODULE_ENABLED = register("module_enabled", new ModuleEnabledCriterion());

    private static <T extends CriterionTrigger<?>> T register(String id, T criterion) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, TrulyRandom.id(id), criterion);
    }

    public static void init() {
        //no-op
    }
}
