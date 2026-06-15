package com.bawnorton.trulyrandom.random.module.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class BlockModelModuleState extends StandardModuleState {
    private boolean forceStatesToUseSameModel;
    private boolean ignoreModelOcclusion;
    private boolean ignoreStateProprties;

    public static final MapCodec<BlockModelModuleState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StandardModuleState.CODEC.fieldOf("standard").forGetter(moduleState -> moduleState),
            Codec.BOOL.optionalFieldOf("force_states_to_use_same_model", false).forGetter(BlockModelModuleState::isForcingStatesToUseSameModel),
            Codec.BOOL.optionalFieldOf("ignore_model_occlusion", false).forGetter(BlockModelModuleState::isIgnoreModelOcclusion),
            Codec.BOOL.optionalFieldOf("ignore_state_properties", false).forGetter(BlockModelModuleState::isIgnoreStateProperties)
    ).apply(instance, BlockModelModuleState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockModelModuleState> STREAM_CODEC = StreamCodec.composite(
            StandardModuleState.STREAM_CODEC,
            moduleState -> moduleState,
            ByteBufCodecs.BOOL,
            BlockModelModuleState::isForcingStatesToUseSameModel,
            ByteBufCodecs.BOOL,
            BlockModelModuleState::isIgnoreModelOcclusion,
            ByteBufCodecs.BOOL,
            BlockModelModuleState::isIgnoreStateProperties,
            BlockModelModuleState::new
    );

    private BlockModelModuleState(StandardModuleState state, boolean forceStatesToUseSameModel, boolean ignoreModelOcclusion, boolean ignoreStateProprties) {
        super(state.isEnabled(), state.isVisible(), state.getSeed());
        this.forceStatesToUseSameModel = forceStatesToUseSameModel;
        this.ignoreModelOcclusion = ignoreModelOcclusion;
        this.ignoreStateProprties = ignoreStateProprties;
    }

    public BlockModelModuleState() {
        super();
        this.forceStatesToUseSameModel = false;
        this.ignoreModelOcclusion = false;
        this.ignoreStateProprties = false;
    }

    @Override
    public Type<?> getType() {
        return ModuleStateTypes.BLOCK_MODEL;
    }

    public void setForceStatesToUseSameModel(boolean forceStatesToUseSameModel) {
        this.forceStatesToUseSameModel = forceStatesToUseSameModel;
    }

    public boolean isForcingStatesToUseSameModel() {
        return forceStatesToUseSameModel;
    }

    public void setIgnoreModelOcclusion(boolean ignoreModelOcclusion) {
        this.ignoreModelOcclusion = ignoreModelOcclusion;
    }

    public boolean isIgnoreModelOcclusion() {
        return ignoreModelOcclusion;
    }

    public void setIgnoreStateProprties(boolean ignoreStateProprties) {
        this.ignoreStateProprties = ignoreStateProprties;
    }

    public boolean isIgnoreStateProperties() {
        return ignoreStateProprties;
    }

    public BlockModelModuleState copy() {
        return new BlockModelModuleState(super.copy(), forceStatesToUseSameModel, ignoreModelOcclusion, ignoreStateProprties);
    }
}
