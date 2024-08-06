package com.bawnorton.trulyrandom.tracker.loot.drop;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.bawnorton.trulyrandom.tracker.loot.LootTableReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
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
import java.util.List;
import java.util.Map;

public class LootTableDrops {
    public static final Map<RegistryKey<LootTable>, LootTableDrops> ALL_DROPS = new HashMap<>();

    public static final Codec<LootTableDrops> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryKey.createCodec(RegistryKeys.LOOT_TABLE)
                    .fieldOf("key")
                    .forGetter(info -> info.lootTableKey),
            Codec.list(Identifier.CODEC.xmap(Registries.ITEM::get, Registries.ITEM::getId))
                    .fieldOf("drops").
                    forGetter(info -> info.drops)
    ).apply(instance, LootTableDrops::new));

    public static final PacketCodec<RegistryByteBuf, LootTableDrops> PACKET_CODEC = PacketCodec.tuple(
            RegistryKey.createPacketCodec(RegistryKeys.LOOT_TABLE), info -> info.lootTableKey,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.registryValue(RegistryKeys.ITEM)), info -> info.drops,
            LootTableDrops::new
    );

    private final RegistryKey<LootTable> lootTableKey;
    private final LootTableIdentifier lootTableId;
    private final List<Item> drops;
    private final DropType dropType;

    private LootTableDrops(RegistryKey<LootTable> key, List<Item> drops) {
        this.lootTableKey = key;
        this.lootTableId = LootTableIdentifier.from(key.getValue());
        this.drops = drops;
        this.dropType = DropTypes.getDropType(LootTableIdentifier.from(key.getValue()));
    }

    public static void populate(Registry<LootTable> lootTableRegistry) {
        long start = System.currentTimeMillis();
        lootTableRegistry.streamEntries().forEach(ref -> ref.getKey().ifPresent(key -> ALL_DROPS.put(key, new LootTableDrops(key, LootTableReader.read(lootTableRegistry, ref.value())))));
        long end = System.currentTimeMillis();
        TrulyRandom.LOGGER.info("Populated {} loot table drops in {}ms", ALL_DROPS.size(), end - start);
    }

    public RegistryKey<LootTable> getKey() {
        return lootTableKey;
    }

    public LootTableIdentifier getLootTableId() {
        return lootTableId;
    }

    public List<Item> getItems() {
        return drops;
    }

    public DropType getDropType() {
        return dropType;
    }

    @Override
    public int hashCode() {
        return lootTableKey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LootTableDrops lootTableDrops) {
            return lootTableKey.equals(lootTableDrops.lootTableKey);
        }
        return false;
    }
}
