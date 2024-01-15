package com.bawnorton.trulyrandom.client.mixin.modernfix.v511minus;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.StateAccessor;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.VersionPredicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import org.embeddedt.modernfix.dynamicresources.ModelLocationCache;
import org.embeddedt.modernfix.util.DynamicOverridableMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = BlockModels.class, priority = 1500)
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, version = @VersionPredicate(max = "5.10.1"))
public abstract class DynamicBlockModelsMixin implements ModelShuffler.BlockStates {
    @Shadow
    private Map<BlockState, BakedModel> models;
    @Shadow
    @Final
    private BakedModelManager modelManager;
    @Unique
    private final Map<BlockState, BlockState> redirectMap = new HashMap<>();

    @Inject(
            method = {
                    "<init>",
                    "setModels"
            },
            at = @At("RETURN")
    )
    private void trulyrandom$initModels(CallbackInfo ci) {
        trulyrandom$resetModels();
    }

    @ModifyVariable(method = "getModel", at = @At("HEAD"), argsOnly = true)
    private BlockState trulyrandom$redirectModel(BlockState state) {
        return redirectMap.getOrDefault(state, state);
    }

    public void trulyrandom$shuffleModels(long seed) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        List<BlockState> blockStates = Registries.BLOCK.stream()
                                                       .map(Block::getStateManager)
                                                       .map(StateManager::getStates)
                                                       .flatMap(Collection::stream)
                                                       .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        trulyrandom$resetModels();
        Map<String, List<BlockState>> propertyMap = new HashMap<>();
        for (BlockState state : blockStates) {
            StringBuilder variant = new StringBuilder();
            for (Map.Entry<Property<?>, Comparable<?>> entry : state.getEntries().entrySet()) {
                variant.append(StateAccessor.getPropertyMapPrinter().apply(entry));
            }
            variant.append(state.isOpaque());
            variant.append(state.getCullingShape(world, BlockPos.ORIGIN));
            propertyMap.computeIfAbsent(variant.toString(), k -> new ArrayList<>()).add(state);
        }
        propertyMap.forEach((k, v) -> v.sort(Comparator.comparingInt(state -> Registries.BLOCK.getRawId(state.getBlock()))));
        Random rnd = new Random(seed);
        for (List<BlockState> variant : propertyMap.values()) {
            Collections.shuffle(variant, rnd);
            for (int i = 0; i < variant.size(); i++) {
                BlockState original = variant.get(i);
                BlockState randomised = variant.get((i + 1) % variant.size());
                redirectMap.put(original, randomised);
            }
        }
    }

    public Map<BlockState, BlockState> trulyrandom$getOriginalRandomisedMap() {
        return redirectMap;
    }

    public void trulyrandom$resetModels() {
        redirectMap.clear();
        models = new DynamicOverridableMap<>(state -> {
            state = redirectMap.getOrDefault(state, state);
            return modelManager.getModel(ModelLocationCache.get(state));
        });
    }

    public boolean trulyrandom$isShuffled() {
        return !redirectMap.isEmpty();
    }
}
