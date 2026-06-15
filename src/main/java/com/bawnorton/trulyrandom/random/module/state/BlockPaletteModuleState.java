package com.bawnorton.trulyrandom.random.module.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class BlockPaletteModuleState extends StandardModuleState {
    private boolean ignoreStateProprties;
    private boolean ignoreCollisionShape;
    private boolean ignoreOcclusionShape;
    private boolean keepStoneAsStone;

    public static final MapCodec<BlockPaletteModuleState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StandardModuleState.CODEC.fieldOf("standard").forGetter(moduleState -> moduleState),
            Codec.BOOL.optionalFieldOf("ignore_state_properties", false).forGetter(BlockPaletteModuleState::isIgnoreStateProperties),
            Codec.BOOL.optionalFieldOf("ignore_collision_shape", false).forGetter(BlockPaletteModuleState::isIgnoreCollisionShape),
            Codec.BOOL.optionalFieldOf("ignore_occlusion_shape", false).forGetter(BlockPaletteModuleState::isIgnoreOcclusionShape),
            Codec.BOOL.optionalFieldOf("keep_stone_as_stone", false).forGetter(BlockPaletteModuleState::isKeepStoneAsStone)
    ).apply(instance, BlockPaletteModuleState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPaletteModuleState> STREAM_CODEC = StreamCodec.composite(
            StandardModuleState.STREAM_CODEC,
            moduleState -> moduleState,
            ByteBufCodecs.BOOL,
            BlockPaletteModuleState::isIgnoreStateProperties,
            ByteBufCodecs.BOOL,
            BlockPaletteModuleState::isIgnoreCollisionShape,
            ByteBufCodecs.BOOL,
            BlockPaletteModuleState::isIgnoreOcclusionShape,
            ByteBufCodecs.BOOL,
            BlockPaletteModuleState::isKeepStoneAsStone,
            BlockPaletteModuleState::new
    );

    private BlockPaletteModuleState(StandardModuleState state, boolean ignoreStateProprties, boolean ignoreCollisionShape, boolean ignoreOcclusionShape, boolean keepStoneAsStone) {
        super(state.isEnabled(), state.isVisible(), state.getSeed());
        this.ignoreStateProprties = ignoreStateProprties;
        this.ignoreCollisionShape = ignoreCollisionShape;
        this.ignoreOcclusionShape = ignoreOcclusionShape;
        this.keepStoneAsStone = keepStoneAsStone;
    }

    public BlockPaletteModuleState() {
        super();
        this.ignoreStateProprties = false;
        this.ignoreCollisionShape = false;
        this.ignoreOcclusionShape = false;
        this.keepStoneAsStone = false;
    }

    @Override
    public Type<?> getType() {
        return ModuleStateTypes.BLOCK_PALETTE;
    }

    public void setIgnoreStateProprties(boolean ignoreStateProprties) {
        this.ignoreStateProprties = ignoreStateProprties;
    }

    public boolean isIgnoreStateProperties() {
        return ignoreStateProprties;
    }

    public void setIgnoreCollisionShape(boolean ignoreCollisionShape) {
        this.ignoreCollisionShape = ignoreCollisionShape;
    }

    public boolean isIgnoreCollisionShape() {
        return ignoreCollisionShape;
    }

    public void setIgnoreOcclusionShape(boolean ignoreOcclusionShape) {
        this.ignoreOcclusionShape = ignoreOcclusionShape;
    }

    public boolean isIgnoreOcclusionShape() {
        return ignoreOcclusionShape;
    }

    public void setKeepStoneAsStone(boolean keepStoneAsStone) {
        this.keepStoneAsStone = keepStoneAsStone;
    }

    public boolean isKeepStoneAsStone() {
        return keepStoneAsStone;
    }

    public BlockPaletteModuleState copy() {
        return new BlockPaletteModuleState(super.copy(), ignoreStateProprties, ignoreCollisionShape, ignoreOcclusionShape, keepStoneAsStone);
    }
}
