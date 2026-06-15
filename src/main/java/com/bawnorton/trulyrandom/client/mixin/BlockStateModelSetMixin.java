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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
    public boolean trulyrandom$forceStatesToUseSameModel() {
        return TrulyRandomClient.getRandomiser().getModules().getState(Module.BLOCK_MODELS, BlockModelModuleState.class).isForcingStatesToUseSameModel();
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

        if (trulyrandom$forceStatesToUseSameModel()) {
            Map<Block, List<BlockState>> statesByBlock = new HashMap<>();
            for (BlockState state : trulyrandom$getBlockStates()) {
                statesByBlock.computeIfAbsent(state.getBlock(), _ -> new ArrayList<>()).add(state);
            }

            Function<BlockState, String> getStateSignature = state -> {
                StringBuilder sb = new StringBuilder();
                if (!trulyrandom$ignoreStateProperties()) {
                    state.getProperties().stream()
                            .sorted(Comparator.comparing(Property::getName))
                            .forEach(p -> sb.append(p.getName()).append("=").append(state.getValue(p)).append(","));
                }
                if (!trulyrandom$ignoreModelOcclusion()) {
                    sb.append("|occ=").append(state.getOcclusionShape());
                }
                return sb.toString();
            };

            Map<String, List<Block>> blocksByStructure = new HashMap<>();
            for (Block block : statesByBlock.keySet()) {
                List<String> stateSignatures = new ArrayList<>();
                for (BlockState state : statesByBlock.get(block)) {
                    stateSignatures.add(getStateSignature.apply(state));
                }
                Collections.sort(stateSignatures);
                String blockStructureKey = String.join("||", stateSignatures);

                blocksByStructure.computeIfAbsent(blockStructureKey, _ -> new ArrayList<>()).add(block);
            }

            for (List<Block> structuralGroup : blocksByStructure.values()) {
                structuralGroup.sort(Comparator.comparingInt(BuiltInRegistries.BLOCK::getId));

                List<Block> shuffledGroup = new ArrayList<>(structuralGroup);
                Collections.shuffle(shuffledGroup, rnd);

                for (int i = 0; i < structuralGroup.size(); i++) {
                    Block originalBlock = structuralGroup.get(i);
                    Block randomizedBlock = shuffledGroup.get(i);

                    List<BlockState> originalStates = statesByBlock.get(originalBlock);
                    List<BlockState> randomizedStates = statesByBlock.get(randomizedBlock);

                    originalStates.sort(Comparator.comparing(getStateSignature));
                    randomizedStates.sort(Comparator.comparing(getStateSignature));

                    for (int j = 0; j < originalStates.size(); j++) {
                        BlockState originalState = originalStates.get(j);
                        BlockState randomizedState = randomizedStates.get(j % randomizedStates.size());
                        trulyrandom$redirectMap.put(originalState, randomizedState);
                    }
                }
            }
        } else {
            for (List<BlockState> variant : buildPropertyMap().values()) {
                Collections.shuffle(variant, rnd);
                for (int i = 0; i < variant.size(); i++) {
                    BlockState original = variant.get(i);
                    BlockState randomised = variant.get((i + 1) % variant.size());
                    trulyrandom$redirectMap.put(original, randomised);
                }
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
