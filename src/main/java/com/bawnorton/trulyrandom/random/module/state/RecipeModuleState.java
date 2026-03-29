package com.bawnorton.trulyrandom.random.module.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.HashMap;
import java.util.Map;

public class RecipeModuleState extends StandardModuleState {
    private final Map<RecipeType<?>, Boolean> enabledRecipeTypes;

    public static final MapCodec<RecipeModuleState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StandardModuleState.CODEC.fieldOf("standard").forGetter(recipeModule -> recipeModule),
            Codec.unboundedMap(BuiltInRegistries.RECIPE_TYPE.byNameCodec(), Codec.BOOL).fieldOf("enabled_recipe_types").forGetter(moduleState -> moduleState.enabledRecipeTypes)
    ).apply(instance, RecipeModuleState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeModuleState> STREAM_CODEC = StreamCodec.composite(
            StandardModuleState.STREAM_CODEC,
            recipeModule -> recipeModule,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.registry(BuiltInRegistries.RECIPE_TYPE.key()), ByteBufCodecs.BOOL),
            moduleState -> moduleState.enabledRecipeTypes,
            RecipeModuleState::new
    );

    private RecipeModuleState(StandardModuleState state, Map<RecipeType<?>, Boolean> enabledRecipeTypes) {
        super(state.isEnabled(), state.isVisible(), state.getSeed());
        this.enabledRecipeTypes = enabledRecipeTypes;
    }

    public RecipeModuleState() {
        super();
        enabledRecipeTypes = new HashMap<>();
        BuiltInRegistries.RECIPE_TYPE.stream().forEach(recipeType -> enabledRecipeTypes.put(recipeType, true));
    }

    @Override
    public Type<?> getType() {
        return ModuleStateTypes.RECIPE;
    }

    public Map<RecipeType<?>, Boolean> getEnabledRecipeTypes() {
        return enabledRecipeTypes;
    }

    public boolean isRecipeTypeEnabled(RecipeType<?> type) {
        return enabledRecipeTypes.get(type);
    }

    public void setRecipeTypeEnabled(RecipeType<?> recipeType, boolean enabled) {
        enabledRecipeTypes.put(recipeType, enabled);
    }

    public RecipeModuleState copy() {
        return new RecipeModuleState(super.copy(), enabledRecipeTypes);
    }
}
