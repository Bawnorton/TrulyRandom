package com.bawnorton.trulyrandom.tracker.loot;

import com.bawnorton.trulyrandom.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.collection.UnaryMap;
import com.bawnorton.trulyrandom.mixin.accessor.VerticallyAttachableBlockItemAccessor;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import java.util.List;

public class LootTableTracker extends Tracker<RegistryKey<LootTable>, RegistryKey<LootTable>> {
    public static final Codec<LootTableTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(
                    RegistryKey.createCodec(RegistryKeys.LOOT_TABLE),
                    RegistryKey.createCodec(RegistryKeys.LOOT_TABLE)
            ).fieldOf("knownLootTables").forGetter(tracker -> tracker.knownLootTables),
                    ItemLootMap.CODEC.fieldOf("itemLootMap").forGetter(tracker -> tracker.itemLootMap),
                    Team.CODEC.fieldOf("team").forGetter(tracker -> tracker.team)
            ).apply(instance, (lootTables, itemToBlockMap, team) -> new LootTableTracker(new UnaryHashMap<>(lootTables), itemToBlockMap, team)));

    public static final PacketCodec<RegistryByteBuf, LootTableTracker> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(
                    UnaryHashMap::new,
                    RegistryKey.createPacketCodec(RegistryKeys.LOOT_TABLE),
                    RegistryKey.createPacketCodec(RegistryKeys.LOOT_TABLE)
            ), tracker -> tracker.knownLootTables,
            ItemLootMap.PACKET_CODEC, tracker -> tracker.itemLootMap,
            Team.PACKET_CODEC, tracker -> tracker.team,
            LootTableTracker::new
    );

    public static final ThreadLocal<Boolean> BROKEN_WITH_SILK = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<List<Team>> LOOT_CAUSERS = ThreadLocal.withInitial(List::of);

    private final UnaryMap<RegistryKey<LootTable>> knownLootTables;
    private final ItemLootMap itemLootMap;

    public LootTableTracker(UnaryMap<RegistryKey<LootTable>> map, ItemLootMap itemLootMap, Team team) {
        super(team);
        this.knownLootTables = map;
        this.itemLootMap = itemLootMap;
    }

    public LootTableTracker() {
        super(null);
        this.knownLootTables = new UnaryHashMap<>();
        this.itemLootMap = new ItemLootMap();
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
            this.markDirty();
        }

        knownLootTables.put(from, to);
        recordSource(from);
    }

    private void recordSource(RegistryKey<LootTable> lootTable) {
        LootTableIdentifier lootTableId = LootTableIdentifier.from(lootTable.getValue());
        if (lootTableId.isFromBlock()) {
            Block sourceBlock = Registries.BLOCK.get(lootTableId.getSourceId());
            Item associatedItem = sourceBlock.asItem();
            recordBlockToItem(sourceBlock, associatedItem);
        }
    }

    private void recordBlockToItem(Block block, Item item) {
        boolean brokeWithSilk = BROKEN_WITH_SILK.get();
        ItemLootMap.Result result = itemLootMap.computeIfAbsent(item, k -> {
            ItemLootMap.Result preResult = ItemLootMap.Result.of(brokeWithSilk, block);
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
    public UnaryMap<RegistryKey<LootTable>> known() {
        return knownLootTables;
    }

    @Override
    public void reset() {
        knownLootTables.clear();
        itemLootMap.clear();
        this.markDirty();
    }
}
