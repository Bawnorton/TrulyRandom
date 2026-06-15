package com.bawnorton.trulyrandom.random.module.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class StructureModuleState extends StandardModuleState {
    private boolean useLegacyRandomiser;
    private boolean nerfElytra;

    public static final MapCodec<StructureModuleState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StandardModuleState.CODEC.fieldOf("standard").forGetter(moduleState -> moduleState),
            Codec.BOOL.optionalFieldOf("use_legacy_randomiser", false).forGetter(StructureModuleState::useLegacyRandomiser),
            Codec.BOOL.optionalFieldOf("nerf_elytra", false).forGetter(StructureModuleState::isNerfElytra)
    ).apply(instance, StructureModuleState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StructureModuleState> STREAM_CODEC = StreamCodec.composite(
            StandardModuleState.STREAM_CODEC,
            moduleState -> moduleState,
            ByteBufCodecs.BOOL,
            StructureModuleState::useLegacyRandomiser,
            ByteBufCodecs.BOOL,
            StructureModuleState::isNerfElytra,
            StructureModuleState::new
    );

    private StructureModuleState(StandardModuleState state, boolean useLegacyRandomiser, boolean nerfElytra) {
        super(state.isEnabled(), state.isVisible(), state.getSeed());
        this.useLegacyRandomiser = useLegacyRandomiser;
        this.nerfElytra = nerfElytra;
    }

    public StructureModuleState() {
        super();
        this.useLegacyRandomiser = false;
        this.nerfElytra = false;
    }

    @Override
    public Type<?> getType() {
        return ModuleStateTypes.STRUCTURE;
    }

    public void setUseLegacyRandomiser(boolean legacyRandomiser) {
        this.useLegacyRandomiser = legacyRandomiser;
    }

    public void setNerfElytra(boolean nerfElytra) {
        this.nerfElytra = nerfElytra;
    }

    public boolean useLegacyRandomiser() {
        return useLegacyRandomiser;
    }

    public boolean isNerfElytra() {
        return nerfElytra;
    }

    public StructureModuleState copy() {
        return new StructureModuleState(super.copy(), useLegacyRandomiser, nerfElytra);
    }
}
