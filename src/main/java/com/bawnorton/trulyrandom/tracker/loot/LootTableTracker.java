package com.bawnorton.trulyrandom.tracker.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.collection.UnaryMap;
import com.bawnorton.trulyrandom.mixin.accessor.VerticallyAttachableBlockItemAccessor;
import com.bawnorton.trulyrandom.tracker.Tracker;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LootTableTracker extends Tracker<RegistryKey<LootTable>, RegistryKey<LootTable>> {
    public static final Codec<LootTableTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(
                    RegistryKey.createCodec(RegistryKeys.LOOT_TABLE),
                    RegistryKey.createCodec(RegistryKeys.LOOT_TABLE)
            ).fieldOf("knownLootTables").forGetter(tracker -> tracker.knownLootTables),
            Codec.unboundedMap(
                    Identifier.CODEC.xmap(Registries.ITEM::get, Registries.ITEM::getId),
                    Identifier.CODEC.xmap(Registries.BLOCK::get, Registries.BLOCK::getId).listOf()
            ).fieldOf("itemToBlockMap").forGetter(tracker -> tracker.itemToBlockMap),
            Uuids.CODEC.fieldOf("playerId").forGetter(tracker -> tracker.playerId)
    ).apply(instance, (lootTables, itemToBlockMap, playerId) -> new LootTableTracker(new UnaryHashMap<>(lootTables), new ItemToBlockMap(itemToBlockMap), playerId)));

    public static final PacketCodec<RegistryByteBuf, LootTableTracker> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.map(
                        UnaryHashMap::new,
                        RegistryKey.createPacketCodec(RegistryKeys.LOOT_TABLE),
                        RegistryKey.createPacketCodec(RegistryKeys.LOOT_TABLE)
                ), tracker -> tracker.knownLootTables,
                PacketCodecs.map(
                        ItemToBlockMap::new,
                        PacketCodecs.registryValue(RegistryKeys.ITEM),
                        PacketCodecs.registryValue(RegistryKeys.BLOCK).collect(PacketCodecs.toList())
                ), tracker -> tracker.itemToBlockMap,
                Uuids.PACKET_CODEC, tracker -> tracker.playerId,
                LootTableTracker::new
    );

    public static final ThreadLocal<List<UUID>> LOOT_CAUSERS = ThreadLocal.withInitial(List::of);

    private final UnaryMap<RegistryKey<LootTable>> knownLootTables;
    private final ItemToBlockMap itemToBlockMap;

    public LootTableTracker(UnaryMap<RegistryKey<LootTable>> map, ItemToBlockMap itemToBlockMap, UUID playerId) {
        super(playerId);
        this.knownLootTables = map;
        this.itemToBlockMap = itemToBlockMap;
    }

    public LootTableTracker() {
        super(null);
        this.knownLootTables = new UnaryHashMap<>();
        this.itemToBlockMap = new ItemToBlockMap();
    }

    public boolean knowsItemLootTable(Item item) {
        return itemToBlockMap.containsKey(item);
    }

    @Override
    public void track(RegistryKey<LootTable> from, RegistryKey<LootTable> to) {
        if(knownLootTables.containsKey(from)) return;

        knownLootTables.put(from, to);
        TrulyRandom.LOGGER.info("Tracking loot table {} -> {}", from, to);

        LootTableIdentifier lootTable = LootTableIdentifier.from(from.getValue());
        if(lootTable.isBlock()) {
            Block sourceBlock = Registries.BLOCK.get(lootTable.getBlockId());
            Item associatedItem = sourceBlock.asItem();
            List<Block> blocks = itemToBlockMap.computeIfAbsent(associatedItem, k -> new ArrayList<>());
            blocks.add(sourceBlock);
            if(associatedItem instanceof VerticallyAttachableBlockItemAccessor accessor) {
                Block wallVariant = accessor.getWallBlock();
                blocks.add(wallVariant);
            }
        }

        this.markDirty();
    }

    @Override
    public Iterable<Map.Entry<RegistryKey<LootTable>, RegistryKey<LootTable>>> known() {
        return knownLootTables.entrySet();
    }

    @Override
    public void reset() {
        knownLootTables.clear();
        itemToBlockMap.clear();
        this.markDirty();
    }
}
