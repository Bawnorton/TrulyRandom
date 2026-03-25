package com.bawnorton.trulyrandom.tracker.loot.drop;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.bawnorton.trulyrandom.tracker.loot.LootTableReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTableDrops {
    public static final Map<ResourceKey<LootTable>, LootTableDrops> ALL_DROPS = new HashMap<>();

    public static final Codec<LootTableDrops> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(Registries.LOOT_TABLE)
                    .fieldOf("key")
                    .forGetter(info -> info.lootTableKey),
            Codec.list(Identifier.CODEC.xmap(BuiltInRegistries.ITEM::getValue, BuiltInRegistries.ITEM::getKey))
                    .fieldOf("drops").
                    forGetter(info -> info.drops)
    ).apply(instance, LootTableDrops::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LootTableDrops> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.LOOT_TABLE), info -> info.lootTableKey,
            ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.registry(Registries.ITEM)), info -> info.drops,
            LootTableDrops::new
    );

    private final ResourceKey<LootTable> lootTableKey;
    private final LootTableIdentifier lootTableId;
    private final List<Item> drops;
    private final DropType dropType;

    private LootTableDrops(ResourceKey<LootTable> key, List<Item> drops) {
        this.lootTableKey = key;
        this.lootTableId = LootTableIdentifier.from(key.identifier());
        this.drops = drops;
        this.dropType = GraphTypes.getDropType(LootTableIdentifier.from(key.identifier()));
    }

    public static void populate(Registry<LootTable> lootTableRegistry) {
        long start = System.currentTimeMillis();
        lootTableRegistry.listElements().forEach(ref -> ref.unwrapKey().ifPresent(key -> ALL_DROPS.put(key, new LootTableDrops(key, LootTableReader.read(lootTableRegistry, ref.value())))));
        long end = System.currentTimeMillis();
        TrulyRandom.LOGGER.info("Populated {} loot table drops in {}ms", ALL_DROPS.size(), end - start);
    }

    public ResourceKey<LootTable> getKey() {
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
