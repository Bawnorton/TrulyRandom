package com.bawnorton.trulyrandom.random.module.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class LootModuleState extends StandardModuleState {
    private boolean useOtherLootTables;

    public static final MapCodec<LootModuleState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            StandardModuleState.CODEC.fieldOf("standard").forGetter(moduleState -> moduleState),
            Codec.BOOL.optionalFieldOf("useOtherLootTables", true).forGetter(LootModuleState::useOtherLootTables)
    ).apply(instance, LootModuleState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LootModuleState> STREAM_CODEC = StreamCodec.composite(
            StandardModuleState.STREAM_CODEC,
            moduleState -> moduleState,
            ByteBufCodecs.BOOL,
            LootModuleState::useOtherLootTables,
            LootModuleState::new
    );

    private LootModuleState(StandardModuleState state, boolean useOtherLootTables) {
        super(state.isEnabled(), state.isVisible(), state.getSeed());
        this.useOtherLootTables = useOtherLootTables;
    }

    public LootModuleState() {
        super();
        this.useOtherLootTables = true;
    }

    @Override
    public Type<?> getType() {
        return ModuleStateTypes.LOOT;
    }

    public void setUseOtherLootTables(boolean useOtherLootTables) {
        this.useOtherLootTables = useOtherLootTables;
    }

    public boolean useOtherLootTables() {
        return useOtherLootTables;
    }

    public LootModuleState copy() {
        return new LootModuleState(super.copy(), useOtherLootTables);
    }

}
