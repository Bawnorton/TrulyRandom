package com.bawnorton.trulyrandom.tracker.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ItemLootMap extends HashMap<Item, ItemLootMap.Result> {
    public static final MapCodec<ItemLootMap> CODEC = Codec.unboundedMap(
            Identifier.CODEC.xmap(Registries.ITEM::get, Registries.ITEM::getId),
            ItemLootMap.Result.CODEC)
            .xmap(ItemLootMap::new, Function.identity())
            .fieldOf("item_loot_map");

    public static final PacketCodec<RegistryByteBuf, ItemLootMap> PACKET_CODEC = PacketCodecs.map(
            ItemLootMap::new,
            PacketCodecs.registryValue(RegistryKeys.ITEM),
            ItemLootMap.Result.PACKET_CODEC
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
                    Identifier.CODEC.xmap(Registries.BLOCK::get, Registries.BLOCK::getId)
                            .listOf()
                            .fieldOf("associatedBlocks")
                            .forGetter(Result::associatedBlocks)
            ).apply(instance, Result::new));

            public static final PacketCodec<RegistryByteBuf, Result> PACKET_CODEC = PacketCodec.tuple(
                    PacketCodecs.BOOLEAN, result -> result.withSilk,
                    PacketCodecs.registryValue(RegistryKeys.BLOCK).collect(PacketCodecs.toList()), Result::associatedBlocks,
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
