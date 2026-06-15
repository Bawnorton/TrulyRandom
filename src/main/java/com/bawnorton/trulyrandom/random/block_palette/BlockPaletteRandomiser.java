package com.bawnorton.trulyrandom.random.block_palette;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.RandomiserModule;
import com.bawnorton.trulyrandom.util.collection.UnaryBiMap;
import com.bawnorton.trulyrandom.util.collection.UnaryHashBiMap;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class BlockPaletteRandomiser extends RandomiserModule {
    private final long seed;
    private final UnaryMap<BlockState> redirectMap = new UnaryHashMap<>();
    private final boolean ignoreStateProperties;
    private final boolean ignoreCollisionShape;
    private final boolean ignoreOcclusionShape;
    private final boolean keepStoneAsStone;

    public BlockPaletteRandomiser(long seed, boolean ignoreStateProperties, boolean ignoreCollisionShape, boolean ignoreOcclusionShape, boolean keepStoneAsStone) {
        this.seed = seed;
        this.ignoreStateProperties = ignoreStateProperties;
        this.ignoreCollisionShape = ignoreCollisionShape;
        this.ignoreOcclusionShape = ignoreOcclusionShape;
        this.keepStoneAsStone = keepStoneAsStone;
        randomise();
    }

    private void randomise() {
        redirectMap.clear();
        List<BlockState> allStates = new ArrayList<>();
        Block.BLOCK_STATE_REGISTRY.iterator().forEachRemaining(allStates::add);
        Random random = new Random(seed);

        Map<Block, List<BlockState>> statesByBlock = new HashMap<>();
        for (BlockState state : allStates) {
            statesByBlock.computeIfAbsent(state.getBlock(), _ -> new ArrayList<>()).add(state);
        }

        Function<BlockState, String> getStateSignature = state -> {
            StringBuilder sb = new StringBuilder();
            if (!ignoreStateProperties) {
                state.getProperties().stream()
                        .sorted(Comparator.comparing(Property::getName))
                        .forEach(p -> sb.append(p.getName()).append("=").append(state.getValue(p)).append(","));
            }
            if (!ignoreCollisionShape) {
                try {
                    VoxelShape shape = state.getCollisionShape(new FakeBlockGetter(state), BlockPos.ZERO);
                    sb.append("|col=").append(shape);
                } catch (RuntimeException e) {
                    sb.append("|col=").append(Shapes.block());
                }
            }
            if (!ignoreOcclusionShape) {
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

        UnaryBiMap<BlockState> preRedirects = new UnaryHashBiMap<>(allStates.size());
        for (List<Block> structuralGroup : blocksByStructure.values()) {
            structuralGroup.sort(Comparator.comparingInt(BuiltInRegistries.BLOCK::getId));

            List<Block> shuffledGroup = new ArrayList<>(structuralGroup);
            Collections.shuffle(shuffledGroup, random);

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
                    preRedirects.put(originalState, randomizedState);
                }
            }
        }

        allStates.forEach(state -> {
            if(isBlacklisted(state, preRedirects.get(state))) {
                BlockState blacklistedState = preRedirects.remove(state);
                BlockState stateBlacklisted = preRedirects.inverse().remove(state);
                preRedirects.put(stateBlacklisted, blacklistedState);
            }
        });

        redirectMap.putAll(preRedirects);
    }

    private boolean isBlacklisted(BlockState state, BlockState randomised) {
        if (state.isAir()) return true;
        if (state.getBlock() instanceof EntityBlock) return true;
        if (keepStoneAsStone && state.is(BlockTags.BASE_STONE_OVERWORLD)) return true;

        return state.is(BlockTags.BASE_STONE_OVERWORLD) && randomised.getBlock() instanceof Fallable;
    }

    public BlockState maybeReplaceBlock(BlockState original) {
        return redirectMap.getOrDefault(original, original);
    }

    @Override
    public Module getModule() {
        return Module.BLOCK_PALETTE;
    }

    private record FakeBlockGetter(BlockState state) implements BlockGetter {
        @Override
        public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
            return null;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            return state;
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return state.getFluidState();
        }

        @Override
        public int getHeight() {
            return 1;
        }

        @Override
        public int getMinY() {
            return 0;
        }
    }
}
