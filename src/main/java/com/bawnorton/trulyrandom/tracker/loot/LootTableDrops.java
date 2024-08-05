package com.bawnorton.trulyrandom.tracker.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
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
import java.util.function.Predicate;

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
    private final List<Item> drops;
    private final DropType dropType;

    private LootTableDrops(RegistryKey<LootTable> key, List<Item> drops) {
        this.lootTableKey = key;
        this.drops = drops;
        this.dropType = determineDropType(key);
    }

    public static void populate(Registry<LootTable> lootTableRegistry) {
        long start = System.currentTimeMillis();
        lootTableRegistry.streamEntries().forEach(ref -> ref.getKey().ifPresent(key -> ALL_DROPS.put(key, new LootTableDrops(key, LootTableReader.read(lootTableRegistry, ref.value())))));
        long end = System.currentTimeMillis();
        TrulyRandom.LOGGER.info("Populated {} loot table drops in {}ms", ALL_DROPS.size(), end - start);
    }

    private DropType determineDropType(RegistryKey<LootTable> key) {
        LootTableIdentifier tableIdentifier = LootTableIdentifier.from(key.getValue());
        for (DropType type : DropType.values()) {
            if (type.test(tableIdentifier)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown loot table type: " + key.getValue());
    }

    public RegistryKey<LootTable> getKey() {
        return lootTableKey;
    }

    public List<Item> getDrops() {
        return drops;
    }

    public DropType getDropType() {
        return dropType;
    }

    public enum DropType implements Predicate<LootTableIdentifier> {
        ARCHAELOGOY(LootTableIdentifier::isFromArchaelogy),
        BLOCK(LootTableIdentifier::isFromBlock),
        CHEST(LootTableIdentifier::isFromChest),
        DISPENSER(LootTableIdentifier::isFromDispenser),
        ENTITY(LootTableIdentifier::isFromEntity),
        EQUIPMENT(LootTableIdentifier::isFromEquipment),
        GAMEPLAY(LootTableIdentifier::isFromGameplay),
        POT(LootTableIdentifier::isFromPot),
        SHEARING(LootTableIdentifier::isFromShearing),
        SPAWNER(LootTableIdentifier::isFromSpawner),
        EMPTY(LootTableIdentifier::isEmpty);

        private final Predicate<LootTableIdentifier> dropPredicate;

        DropType(Predicate<LootTableIdentifier> dropPredicate) {
            this.dropPredicate = dropPredicate;
        }

        public boolean test(LootTableIdentifier tableIdentifier) {
            return dropPredicate.test(tableIdentifier);
        }
    }
}
