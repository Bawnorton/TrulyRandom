package com.bawnorton.trulyrandom.tracker.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.collection.UnaryBiMap;
import com.bawnorton.trulyrandom.collection.UnaryHashBiMap;
import com.bawnorton.trulyrandom.extend.LookupExtender;
import com.bawnorton.trulyrandom.mixin.accessor.VerticallyAttachableBlockItemAccessor;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import com.bawnorton.trulyrandom.tracker.loot.drop.SilkQuery;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class LootTableTracker extends Tracker<RegistryKey<LootTable>, RegistryKey<LootTable>> {
    public static final Codec<LootTableTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(
                    RegistryKey.createCodec(RegistryKeys.LOOT_TABLE),
                    RegistryKey.createCodec(RegistryKeys.LOOT_TABLE))
                    .fieldOf("known_loot_tables")
                    .forGetter(tracker -> tracker.knownLootTables),
            ItemLootMap.CODEC
                    .fieldOf("item_loot_map")
                    .forGetter(tracker -> tracker.itemLootMap),
            Codec.unboundedMap(
                    Identifier.CODEC,
                    Codec.list(LootTableDrops.CODEC).xmap(list -> (Set<LootTableDrops>) new HashSet<>(list), ArrayList::new))
                    .fieldOf("source_map")
                    .forGetter(tracker -> tracker.sourceMap),
            Team.CODEC
                    .fieldOf("team")
                    .forGetter(tracker -> tracker.team)
            ).apply(instance, (lootTables, itemLootMap, infoMap, team) -> new LootTableTracker(new UnaryHashBiMap<>(lootTables), itemLootMap, new HashMap<>(infoMap), team)));

    public static final PacketCodec<RegistryByteBuf, LootTableTracker> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(
                    UnaryHashBiMap::new,
                    RegistryKey.createPacketCodec(RegistryKeys.LOOT_TABLE),
                    RegistryKey.createPacketCodec(RegistryKeys.LOOT_TABLE)
            ), tracker -> tracker.knownLootTables,
            ItemLootMap.PACKET_CODEC, tracker -> tracker.itemLootMap,
            PacketCodecs.map(
                    HashMap::new,
                    Identifier.PACKET_CODEC,
                    PacketCodecs.collection(HashSet::new, LootTableDrops.PACKET_CODEC)
            ), tracker -> tracker.sourceMap,
            Team.PACKET_CODEC, tracker -> tracker.team,
            LootTableTracker::new
    );

    public static final ThreadLocal<Boolean> BROKEN_WITH_SILK = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<List<Team>> LOOT_CAUSERS = ThreadLocal.withInitial(List::of);

    private final UnaryBiMap<RegistryKey<LootTable>> knownLootTables;
    private final ItemLootMap itemLootMap;
    private final Map<Identifier, Set<LootTableDrops>> sourceMap;

    private Registry<LootTable> lootTableRegistry;

    public LootTableTracker(UnaryBiMap<RegistryKey<LootTable>> map, ItemLootMap itemLootMap, Map<Identifier, Set<LootTableDrops>> sourceMap, Team team) {
        super(team);
        this.knownLootTables = map;
        this.itemLootMap = itemLootMap;
        this.sourceMap = sourceMap;
    }

    public LootTableTracker() {
        super(null);
        this.knownLootTables = new UnaryHashBiMap<>();
        this.itemLootMap = new ItemLootMap();
        this.sourceMap = new HashMap<>();
    }

    public static <T> T attachCause(Supplier<T> toAttach, List<Team> teams) {
        LOOT_CAUSERS.set(teams);
        T result = toAttach.get();
        LOOT_CAUSERS.remove();
        return result;
    }

    public void setLootTableRegistry(Registry<LootTable> registry) {
        this.lootTableRegistry = registry;
    }

    public boolean knowsItemLootTable(Item item) {
        if (notOfBlock(item)) {
            return true;
        }

        return itemLootMap.containsKey(item);
    }

    public boolean brokeWithSilk(Item item) {
        if (notOfBlock(item)) {
            return true;
        }

        return itemLootMap.brokeWithSilk(item);
    }

    private boolean notOfBlock(Item item) {
        if (!(item instanceof BlockItem)) {
            return true;
        }
        return item == Items.AIR;
    }

    public boolean knowsBlockLootTable(Block block) {
        return itemLootMap.seenBlock(block);
    }

    public boolean brokeWithSilk(Block block) {
        return itemLootMap.brokeWithSilk(block);
    }

    @Override
    public void track(RegistryKey<LootTable> from, RegistryKey<LootTable> to) {
        if (!knownLootTables.containsKey(from)) {
            knownLootTables.put(from, to);
            this.markDirty();
        }

        LootTable table = ((LookupExtender) TrulyRandom.getServer().getReloadableRegistries()).trulyrandom$getUnalteredLootTable(to);
        SilkQuery silkQuery = LootTableReader.queryForSilk(lootTableRegistry, table);
        recordSource(from, silkQuery);
        if (to == null) return;

        LootTableDrops drops = getDrops(to);
        drops.getItems().forEach(item -> {
            if(silkQuery.needsSilk(item) && !BROKEN_WITH_SILK.get()) return;

            Identifier itemId = Registries.ITEM.getId(item);
            if(sourceMap.computeIfAbsent(itemId, k -> new HashSet<>()).add(drops)) {
                markDirty();
            }
        });
    }

    private void recordSource(RegistryKey<LootTable> lootTable, SilkQuery silkQuery) {
        LootTableIdentifier lootTableId = LootTableIdentifier.from(lootTable.getValue());
        if (lootTableId.isFromBlock()) {
            Block sourceBlock = Registries.BLOCK.get(lootTableId.getSourceId());
            Item associatedItem = sourceBlock.asItem();
            recordBlockToItem(sourceBlock, associatedItem, silkQuery);
        }
    }

    private void recordBlockToItem(Block block, Item item, SilkQuery silkQuery) {
        boolean brokeWithSilk = BROKEN_WITH_SILK.get();
        ItemLootMap.Result result = itemLootMap.computeIfAbsent(item, k -> {
            ItemLootMap.Result preResult = ItemLootMap.Result.of(brokeWithSilk, block);
            if(!silkQuery.hasAnyThatNeedSilk()) preResult.withSilk = true;
            if (item instanceof VerticallyAttachableBlockItemAccessor accessor) {
                Block wallVariant = accessor.getWallBlock();
                preResult.addBlock(wallVariant);
            }
            return preResult;
        });
        if (!result.withSilk && brokeWithSilk) {
            result.withSilk = true;
            this.markDirty();
        }
    }

    @Override
    public UnaryBiMap<RegistryKey<LootTable>> known() {
        return knownLootTables;
    }

    public Optional<RegistryKey<LootTable>> getFrom(RegistryKey<LootTable> to) {
        return Optional.ofNullable(knownLootTables.inverse().get(to));
    }

    public Optional<RegistryKey<LootTable>> getTo(RegistryKey<LootTable> from) {
        return Optional.ofNullable(knownLootTables.get(from));
    }

    public LootTableDrops getDrops(RegistryKey<LootTable> key) {
        return LootTableDrops.ALL_DROPS.get(key);
    }

    public List<Item> getAllDrops() {
        return sourceMap.keySet().stream().map(Registries.ITEM::get).toList();
    }

    public Set<LootTableDrops> getSources(Item item) {
        return sourceMap.getOrDefault(Registries.ITEM.getId(item), Set.of());
    }

    @Override
    public void reset() {
        knownLootTables.clear();
        itemLootMap.clear();
        sourceMap.clear();
        this.markDirty();
    }
}
