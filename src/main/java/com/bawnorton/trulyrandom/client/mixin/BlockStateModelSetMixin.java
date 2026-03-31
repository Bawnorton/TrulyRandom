package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.state.BlockModelModuleState;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

@MixinEnvironment("client")
@Mixin(BlockStateModelSet.class)
abstract class BlockStateModelSetMixin implements ModelShuffler.BlockStates {
    @Unique
    private final UnaryMap<BlockState> trulyrandom$redirectMap = new UnaryHashMap<>();
    @Final
    @Shadow
    private Map<BlockState, BlockModel> modelByState;

    @WrapOperation(method = "get", at = @At(value = "INVOKE", target = "Ljava/util/Map;getOrDefault(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <K, V> Object getShuffledModel(Map<K, V> instance, Object key, V defaultValue, Operation<V> original) {
        BlockState redirected = trulyrandom$redirectMap.getOrDefault((BlockState) key, (BlockState) key);
        return original.call(instance, redirected, defaultValue);
    }

    @Override
    public List<BlockState> trulyrandom$getBlockStates() {
        return new ArrayList<>(modelByState.keySet());
    }

    @Override
    public boolean trulyrandom$ignoreModelOcclusion() {
        return TrulyRandomClient.getRandomiser().getModules().getState(Module.BLOCK_MODELS, BlockModelModuleState.class).isIgnoreModelOcclusion();
    }

    @Override
    public boolean trulyrandom$ignoreStateProperties() {
        return TrulyRandomClient.getRandomiser().getModules().getState(Module.BLOCK_MODELS, BlockModelModuleState.class).isIgnoreStateProperties();
    }

    @Override
    public void trulyrandom$shuffleModels(long seed) {
        if (modelByState == null) return;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        trulyrandom$resetModels();

        Random rnd = new Random(seed);
        for (List<BlockState> variant : buildPropertyMap().values()) {
            Collections.shuffle(variant, rnd);
            for (int i = 0; i < variant.size(); i++) {
                BlockState original = variant.get(i);
                BlockState randomised = variant.get((i + 1) % variant.size());
                trulyrandom$redirectMap.put(original, randomised);
            }
        }
    }

    @Override
    public UnaryMap<BlockState> trulyrandom$getRedirectMap() {
        return trulyrandom$redirectMap;
    }

    @Override
    public void trulyrandom$resetModels() {
        trulyrandom$redirectMap.clear();
    }

    @Override
    public boolean trulyrandom$isShuffled() {
        return !trulyrandom$redirectMap.isEmpty();
    }
}
