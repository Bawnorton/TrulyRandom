package com.bawnorton.trulyrandom.client.mixin.modernfix.v511plus;

import com.bawnorton.mixinsquared.TargetHandler;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.StateAccessor;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.VersionPredicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import org.embeddedt.modernfix.dynamicresources.DynamicModelCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(value = BlockModels.class, priority = 1500)
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, version = @VersionPredicate(min = "5.11"))
public abstract class DynamicBlockModelsMixin implements ModelShuffler.BlockStates {
    @Unique
    private final Map<BlockState, BlockState> redirectMap = new HashMap<>();

    @SuppressWarnings("MixinAnnotationTarget")
    @Shadow
    @Final
    private DynamicModelCache<BlockState> mfix$modelCache;

    @TargetHandler(
            mixin = "org.embeddedt.modernfix.common.mixin.perf.dynamic_resources.BlockModelShaperMixin",
            name = "lambda$new$0"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/render/block/BlockModels.cacheBlockModel (Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/model/BakedModel;"
            )
    )
    private BakedModel getShuffledModel(BlockModels instance, BlockState blockState, Operation<BakedModel> original) {
        BlockState redirected = redirectMap.getOrDefault(blockState, blockState);
        return original.call(instance, redirected);
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
        mfix$modelCache.clear();
    }

    public boolean trulyrandom$isShuffled() {
        return !redirectMap.isEmpty();
    }
}
