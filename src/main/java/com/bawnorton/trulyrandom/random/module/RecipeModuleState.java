package com.bawnorton.trulyrandom.random.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import java.util.HashMap;
import java.util.Map;

public class RecipeModuleState extends StandardModuleState {
    private final Map<RecipeType<?>, Boolean> enabledRecipeTypes;

    public static final MapCodec<RecipeModuleState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StandardModuleState.CODEC.fieldOf("standard").forGetter(recipeModule -> recipeModule),
            Codec.unboundedMap(Registries.RECIPE_TYPE.getCodec(), Codec.BOOL).fieldOf("enabled_recipe_types").forGetter(moduleState -> moduleState.enabledRecipeTypes)
    ).apply(instance, RecipeModuleState::new));

    public static final PacketCodec<RegistryByteBuf, RecipeModuleState> PACKET_CODEC = PacketCodec.tuple(
            StandardModuleState.PACKET_CODEC,
            recipeModule -> recipeModule,
            PacketCodecs.map(HashMap::new, PacketCodecs.registryCodec(Registries.RECIPE_TYPE.getCodec()), PacketCodecs.BOOL),
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
        Registries.RECIPE_TYPE.stream().forEach(recipeType -> enabledRecipeTypes.put(recipeType, true));
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
