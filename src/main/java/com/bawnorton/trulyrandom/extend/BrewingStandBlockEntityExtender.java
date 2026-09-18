package com.bawnorton.trulyrandom.extend;

import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Map;

public interface BrewingStandBlockEntityExtender {
    Map<Integer, RecipeHolder<?>> trulyrandom$RECIPES();
}
