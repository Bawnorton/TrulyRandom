package com.bawnorton.trulyrandom.tracker.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ItemLootMap extends HashMap<Item, ItemLootMap.Result> {
    public static final MapCodec<ItemLootMap> CODEC = Codec.unboundedMap(
            Identifier.CODEC.xmap(BuiltInRegistries.ITEM::getValue, BuiltInRegistries.ITEM::getKey),
            ItemLootMap.Result.CODEC)
            .xmap(ItemLootMap::new, Function.identity())
            .fieldOf("item_loot_map");

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemLootMap> STREAM_CODEC = ByteBufCodecs.map(
            ItemLootMap::new,
            ByteBufCodecs.registry(Registries.ITEM),
            ItemLootMap.Result.STREAM_CODEC
    );

    public ItemLootMap() {
        super();
    }

    public ItemLootMap(Map<Item, ItemLootMap.Result> map) {
        super(map);
    }

    public ItemLootMap(int initialCapacity) {
        super(initialCapacity);
    }

    public boolean seenBlock(Block block) {
        return values().stream().anyMatch(result -> result.associatedBlocks.contains(block));
    }

    public boolean brokeWithSilk(Item item) {
        if(!containsKey(item)) return false;

        return get(item).withSilk;
    }

    public boolean brokeWithSilk(Block block) {
        return values().stream().anyMatch(result -> result.associatedBlocks.contains(block) && result.withSilk);
    }

    public static final class Result {
            public static final Codec<Result> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.BOOL.fieldOf("withSilk").forGetter(result -> result.withSilk),
                    Identifier.CODEC.xmap(BuiltInRegistries.BLOCK::getValue, BuiltInRegistries.BLOCK::getKey)
                            .listOf()
                            .fieldOf("associatedBlocks")
                            .forGetter(Result::associatedBlocks)
            ).apply(instance, Result::new));

            public static final StreamCodec<RegistryFriendlyByteBuf, Result> STREAM_CODEC = StreamCodec.composite(
                    ByteBufCodecs.BOOL, result -> result.withSilk,
                    ByteBufCodecs.registry(Registries.BLOCK).apply(ByteBufCodecs.list()), Result::associatedBlocks,
                    Result::new
            );
        private final List<Block> associatedBlocks;
        public boolean withSilk;

        public Result(boolean withSilk, List<Block> associatedBlocks) {
            this.withSilk = withSilk;
            this.associatedBlocks = associatedBlocks;
        }

        public static Result of(boolean withSilk, List<Block> associatedBlocks) {
            return new Result(withSilk, associatedBlocks);
        }

        public static Result of(boolean withSilk, Block... associatedBlocks) {
            return new Result(withSilk, new ArrayList<>(List.of(associatedBlocks)));
        }

        public static Result of(boolean withSilk) {
            return new Result(withSilk, new ArrayList<>());
        }

        public void addBlock(Block block) {
            associatedBlocks.add(block);
        }

        public List<Block> associatedBlocks() {
            return associatedBlocks;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (Result) obj;
            return this.withSilk == that.withSilk &&
                    Objects.equals(this.associatedBlocks, that.associatedBlocks);
        }

        @Override
        public int hashCode() {
            return Objects.hash(withSilk, associatedBlocks);
        }
    }
}
