package com.bawnorton.trulyrandom.tracker.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.LookupExtender;
import com.bawnorton.trulyrandom.mixin.accessor.StandingAndWallBlockItemAccessor;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import com.bawnorton.trulyrandom.tracker.loot.drop.SilkQuery;
import com.bawnorton.trulyrandom.util.collection.UnaryBiMap;
import com.bawnorton.trulyrandom.util.collection.UnaryHashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class LootTableTracker extends Tracker<ResourceKey<LootTable>, ResourceKey<LootTable>> {
    public static final Codec<LootTableTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(
                    ResourceKey.codec(Registries.LOOT_TABLE),
                    ResourceKey.codec(Registries.LOOT_TABLE))
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

    public static final StreamCodec<RegistryFriendlyByteBuf, LootTableTracker> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    UnaryHashBiMap::new,
                    ResourceKey.streamCodec(Registries.LOOT_TABLE),
                    ResourceKey.streamCodec(Registries.LOOT_TABLE)
            ), tracker -> tracker.knownLootTables,
            ItemLootMap.STREAM_CODEC, tracker -> tracker.itemLootMap,
            ByteBufCodecs.map(
                    HashMap::new,
                    Identifier.STREAM_CODEC,
                    ByteBufCodecs.collection(HashSet::new, LootTableDrops.STREAM_CODEC)
            ), tracker -> tracker.sourceMap,
            Team.STREAM_CODEC, tracker -> tracker.team,
            LootTableTracker::new
    );

    public static final ThreadLocal<Boolean> BROKEN_WITH_SILK = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<List<Team>> LOOT_CAUSERS = ThreadLocal.withInitial(List::of);

    private final UnaryBiMap<ResourceKey<LootTable>> knownLootTables;
    private final ItemLootMap itemLootMap;
    private final Map<Identifier, Set<LootTableDrops>> sourceMap;

    private HolderGetter<LootTable> lootTableRegistry;

    public LootTableTracker(UnaryBiMap<ResourceKey<LootTable>> map, ItemLootMap itemLootMap, Map<Identifier, Set<LootTableDrops>> sourceMap, Team team) {
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

    public void setLootTableRegistry(HolderGetter<LootTable> registry) {
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
    public void track(ResourceKey<LootTable> from, ResourceKey<LootTable> to) {
        if (!knownLootTables.containsKey(from)) {
            knownLootTables.put(from, to);
            this.markDirty();
        }

        LootTable table = ((LookupExtender) TrulyRandom.getServer().reloadableRegistries()).trulyrandom$getUnalteredLootTable(to);
        SilkQuery silkQuery = LootTableReader.queryForSilk(lootTableRegistry, table);
        recordSource(from, silkQuery);
        if (to == null) return;

        LootTableDrops drops = getDrops(to);
        drops.getItems().forEach(item -> {
            if(silkQuery.needsSilk(item) && !BROKEN_WITH_SILK.get()) return;

            Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
            if(sourceMap.computeIfAbsent(itemId, _ -> new HashSet<>()).add(drops)) {
                markDirty();
            }
        });
    }

    private void recordSource(ResourceKey<LootTable> lootTable, SilkQuery silkQuery) {
        LootTableIdentifier lootTableId = LootTableIdentifier.from(lootTable.identifier());
        if (lootTableId.isFromBlock()) {
            Block sourceBlock = BuiltInRegistries.BLOCK.getValue(lootTableId.getSourceId());
            Item associatedItem = sourceBlock.asItem();
            recordBlockToItem(sourceBlock, associatedItem, silkQuery);
        }
    }

    private void recordBlockToItem(Block block, Item item, SilkQuery silkQuery) {
        boolean brokeWithSilk = BROKEN_WITH_SILK.get();
        ItemLootMap.Result result = itemLootMap.computeIfAbsent(item, k -> {
            ItemLootMap.Result preResult = ItemLootMap.Result.of(brokeWithSilk, block);
            if(!silkQuery.hasAnyThatNeedSilk()) preResult.withSilk = true;
            if (item instanceof StandingAndWallBlockItemAccessor accessor) {
                Block wallVariant = accessor.trulyrandom$wallBlock();
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
    public UnaryBiMap<ResourceKey<LootTable>> known() {
        return knownLootTables;
    }

    public Optional<ResourceKey<LootTable>> getFrom(ResourceKey<LootTable> to) {
        return Optional.ofNullable(knownLootTables.inverse().get(to));
    }

    public Optional<ResourceKey<LootTable>> getTo(ResourceKey<LootTable> from) {
        return Optional.ofNullable(knownLootTables.get(from));
    }

    public LootTableDrops getDrops(ResourceKey<LootTable> key) {
        return LootTableDrops.ALL_DROPS.get(key);
    }

    public List<Item> getAllDrops() {
        return sourceMap.keySet().stream().map(BuiltInRegistries.ITEM::getValue).toList();
    }

    public Set<LootTableDrops> getSources(Item item) {
        return sourceMap.getOrDefault(BuiltInRegistries.ITEM.getKey(item), Set.of());
    }

    public boolean knowsSource(Item item) {
        return sourceMap.containsKey(BuiltInRegistries.ITEM.getKey(item));
    }

    @Override
    public void reset() {
        knownLootTables.clear();
        itemLootMap.clear();
        sourceMap.clear();
        this.markDirty();
    }
}
