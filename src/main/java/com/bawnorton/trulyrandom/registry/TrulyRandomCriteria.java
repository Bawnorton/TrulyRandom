package com.bawnorton.trulyrandom.registry;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.data.advancement.criterion.CraftingCriterion;
import com.bawnorton.trulyrandom.data.advancement.criterion.ModuleEnabledCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class TrulyRandomCriteria {
    public static final CraftingCriterion CRAFTING = register("crafting", new CraftingCriterion());
    public static final ModuleEnabledCriterion MODULE_ENABLED = register("module_enabled", new ModuleEnabledCriterion());

    private static <T extends AbstractCriterion<?>> T register(String id, T criterion) {
        return Registry.register(Registries.CRITERION, TrulyRandom.id(id), criterion);
    }

    public static void init() {
        //no-op
    }
}
