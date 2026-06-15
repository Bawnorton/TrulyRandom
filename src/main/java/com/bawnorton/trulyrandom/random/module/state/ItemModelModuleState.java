package com.bawnorton.trulyrandom.random.module.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class ItemModelModuleState extends StandardModuleState {
    private boolean matchBlockModelRandomisation;

    public static final MapCodec<ItemModelModuleState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StandardModuleState.CODEC.fieldOf("standard").forGetter(moduleState -> moduleState),
            Codec.BOOL.optionalFieldOf("match_block_model_randomisation", false).forGetter(ItemModelModuleState::isMatchingBlockModelRandomisation)
    ).apply(instance, ItemModelModuleState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemModelModuleState> STREAM_CODEC = StreamCodec.composite(
            StandardModuleState.STREAM_CODEC,
            moduleState -> moduleState,
            ByteBufCodecs.BOOL,
            ItemModelModuleState::isMatchingBlockModelRandomisation,
            ItemModelModuleState::new
    );

    private ItemModelModuleState(StandardModuleState state, boolean matchBlockModelRandomisation) {
        super(state.isEnabled(), state.isVisible(), state.getSeed());
        this.matchBlockModelRandomisation = matchBlockModelRandomisation;
    }

    public ItemModelModuleState() {
        super();
        this.matchBlockModelRandomisation = false;
    }

    @Override
    public Type<?> getType() {
        return ModuleStateTypes.ITEM_MODEL;
    }

    public void setMatchBlockModelRandomisation(boolean matchBlockModelRandomisation) {
        this.matchBlockModelRandomisation = matchBlockModelRandomisation;
    }

    public boolean isMatchingBlockModelRandomisation() {
        return matchBlockModelRandomisation;
    }

    public ItemModelModuleState copy() {
        return new ItemModelModuleState(super.copy(), matchBlockModelRandomisation);
    }

}
