package com.bawnorton.trulyrandom.random.module.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class BlockModelModuleState extends StandardModuleState {
    private boolean ignoreModelOcclusion;

    public static final MapCodec<BlockModelModuleState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StandardModuleState.CODEC.fieldOf("standard").forGetter(moduleState -> moduleState),
            Codec.BOOL.fieldOf("ignore_model_occlusion").forGetter(BlockModelModuleState::isIgnoreModelOcclusion)
    ).apply(instance, BlockModelModuleState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockModelModuleState> STREAM_CODEC = StreamCodec.composite(
            StandardModuleState.STREAM_CODEC,
            moduleState -> moduleState,
            ByteBufCodecs.BOOL,
            BlockModelModuleState::isIgnoreModelOcclusion,
            BlockModelModuleState::new
    );

    private BlockModelModuleState(StandardModuleState state, boolean ignoreModelOcclusion) {
        super(state.isEnabled(), state.isVisible(), state.getSeed());
        this.ignoreModelOcclusion = ignoreModelOcclusion;
    }

    public BlockModelModuleState() {
        super();
        this.ignoreModelOcclusion = false;
    }

    @Override
    public Type<?> getType() {
        return ModuleStateTypes.BLOCK_MODEL;
    }

    public void setIgnoreModelOcclusion(boolean ignoreModelOcclusion) {
        this.ignoreModelOcclusion = ignoreModelOcclusion;
    }

    public boolean isIgnoreModelOcclusion() {
        return ignoreModelOcclusion;
    }

    public BlockModelModuleState copy() {
        return new BlockModelModuleState(super.copy(), ignoreModelOcclusion);
    }
}
