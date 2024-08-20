package com.bawnorton.trulyrandom.random.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.DataResult;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class LootRandomiser extends ServerRandomiserModule {
    private final Map<Team, LootTableTracker> trackers = new HashMap<>();
    private final Map<RegistryKey<LootTable>, LootTable> originalLootTables = new HashMap<>();
    private final UnaryMap<RegistryKey<LootTable>> redirectMap = new UnaryHashMap<>();
    private final Registry<LootTable> lootTableRegistry;

    private final Set<RegistryKey<LootTable>> blacklist = Set.of(
            Blocks.SHULKER_BOX.getLootTableKey(),
            Blocks.WHITE_SHULKER_BOX.getLootTableKey(),
            Blocks.ORANGE_SHULKER_BOX.getLootTableKey(),
            Blocks.MAGENTA_SHULKER_BOX.getLootTableKey(),
            Blocks.LIGHT_BLUE_SHULKER_BOX.getLootTableKey(),
            Blocks.YELLOW_SHULKER_BOX.getLootTableKey(),
            Blocks.LIME_SHULKER_BOX.getLootTableKey(),
            Blocks.PINK_SHULKER_BOX.getLootTableKey(),
            Blocks.GRAY_SHULKER_BOX.getLootTableKey(),
            Blocks.LIGHT_GRAY_SHULKER_BOX.getLootTableKey(),
            Blocks.CYAN_SHULKER_BOX.getLootTableKey(),
            Blocks.PURPLE_SHULKER_BOX.getLootTableKey(),
            Blocks.BLUE_SHULKER_BOX.getLootTableKey(),
            Blocks.BROWN_SHULKER_BOX.getLootTableKey(),
            Blocks.GREEN_SHULKER_BOX.getLootTableKey(),
            Blocks.RED_SHULKER_BOX.getLootTableKey(),
            Blocks.BLACK_SHULKER_BOX.getLootTableKey()
    );

    public LootRandomiser(MinecraftServer server) {
        lootTableRegistry = server.getReloadableRegistries()
                .getRegistryManager()
                .get(RegistryKeys.LOOT_TABLE);
        Set<RegistryKey<LootTable>> keys = lootTableRegistry.getKeys();
        keys.forEach(key -> {
            LootTable lootTable = lootTableRegistry.get(key);
            originalLootTables.put(key, lootTable);
        });
    }

    public RegistryKey<LootTable> getLootTable(@NotNull List<Team> teams, RegistryKey<LootTable> key) {
        RegistryKey<LootTable> result = redirectMap.getOrDefault(key, key);
        if (!result.equals(key)) {
            for (Team team : teams) {
                trackers.computeIfAbsent(team, k -> {
                    LootTableTracker tracker = new LootTableTracker();
                    tracker.setLootTableRegistry(lootTableRegistry);
                    tracker.setTeam(team);
                    return tracker;
                }).track(key, result);
            }
        }
        LootTableTracker.BROKEN_WITH_SILK.remove();
        return result;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList trackerNbt = new NbtList();
        trackers.forEach((team, tracker) -> {
            DataResult<NbtElement> result = LootTableTracker.CODEC.encodeStart(NbtOps.INSTANCE, tracker);
            result.result().ifPresent(trackerNbt::add);
            result.error().ifPresent(e -> TrulyRandom.LOGGER.error(e.message()));
        });
        nbt.put("trackers", trackerNbt);
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        NbtList trackerNbt = nbt.getList("trackers", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : trackerNbt) {
            DataResult<LootTableTracker> result = LootTableTracker.CODEC.parse(NbtOps.INSTANCE, element);
            result.result().ifPresent(tracker -> {
                trackers.put(tracker.getTeam(), tracker);
                tracker.setLootTableRegistry(lootTableRegistry);
            });
            result.error().ifPresent(e -> TrulyRandom.LOGGER.error(e.message()));
        }
    }

    @Override
    public void randomise(MinecraftServer server, long seed) {
        List<RegistryKey<LootTable>> keys = new ArrayList<>(originalLootTables.keySet());
        keys.sort(Comparator.comparing(RegistryKey::getValue));
        Random random = new Random(seed);
        Collections.shuffle(keys, random);
        BiMap<RegistryKey<LootTable>, RegistryKey<LootTable>> preRedirects = HashBiMap.create(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            RegistryKey<LootTable> originalKey = keys.get(i);
            RegistryKey<LootTable> randomKey = keys.get((i + 1) % keys.size());
            preRedirects.put(originalKey, randomKey);
        }
        blacklist.forEach(key -> {
            RegistryKey<LootTable> blacklistedDrops = preRedirects.remove(key);
            RegistryKey<LootTable> dropsBlacklisted = preRedirects.inverse().remove(key);
            preRedirects.put(dropsBlacklisted, blacklistedDrops);
        });
        redirectMap.putAll(preRedirects);
    }

    @Override
    public void reset(MinecraftServer server) {
        redirectMap.clear();
    }

    @Override
    public @Nullable LootTableTracker getTracker(TeamMember teamMember) {
        return trackers.get(teamMember.trulyrandom$getTeam());
    }

    @Override
    public List<LootTableTracker> getTrackers() {
        return new ArrayList<>(trackers.values());
    }

    @Override
    public Module getModule() {
        return Module.LOOT_TABLES;
    }
}
