package com.bawnorton.trulyrandom.random.module.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class StructureModuleState extends StandardModuleState {
    private boolean replaceStructuresInstead;

    public static final MapCodec<StructureModuleState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StandardModuleState.CODEC.fieldOf("standard").forGetter(moduleState -> moduleState),
            Codec.BOOL.optionalFieldOf("replace_structures_instead", false).forGetter(StructureModuleState::replaceStructuresInstead)
    ).apply(instance, StructureModuleState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StructureModuleState> STREAM_CODEC = StreamCodec.composite(
            StandardModuleState.STREAM_CODEC,
            moduleState -> moduleState,
            ByteBufCodecs.BOOL,
            StructureModuleState::replaceStructuresInstead,
            StructureModuleState::new
    );

    private StructureModuleState(StandardModuleState state, boolean replaceStructuresInstead) {
        super(state.isEnabled(), state.isVisible(), state.getSeed());
        this.replaceStructuresInstead = replaceStructuresInstead;
    }

    public StructureModuleState() {
        super();
        this.replaceStructuresInstead = false;
    }

    @Override
    public Type<?> getType() {
        return ModuleStateTypes.STRUCTURE;
    }

    public void setReplaceStructuresInstead(boolean replaceStructuresInstead) {
        this.replaceStructuresInstead = replaceStructuresInstead;
    }

    public boolean replaceStructuresInstead() {
        return replaceStructuresInstead;
    }

    public StructureModuleState copy() {
        return new StructureModuleState(super.copy(), replaceStructuresInstead);
    }
}
