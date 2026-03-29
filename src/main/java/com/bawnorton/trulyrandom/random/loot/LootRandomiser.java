package com.bawnorton.trulyrandom.random.loot;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.util.collection.UnaryBiMap;
import com.bawnorton.trulyrandom.util.collection.UnaryHashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LootRandomiser extends ServerRandomiserModule {
    private final Map<Team, LootTableTracker> trackers = new HashMap<>();
    private final Map<ResourceKey<LootTable>, LootTable> originalLootTables = new HashMap<>();
    private final UnaryBiMap<ResourceKey<LootTable>> redirectMap = new UnaryHashBiMap<>();
    private final Registry<LootTable> lootTableRegistry;

    private final Set<ResourceKey<LootTable>> blacklist = Stream.of(
            Blocks.SHULKER_BOX.getLootTable(),
            Blocks.WHITE_SHULKER_BOX.getLootTable(),
            Blocks.ORANGE_SHULKER_BOX.getLootTable(),
            Blocks.MAGENTA_SHULKER_BOX.getLootTable(),
            Blocks.LIGHT_BLUE_SHULKER_BOX.getLootTable(),
            Blocks.YELLOW_SHULKER_BOX.getLootTable(),
            Blocks.LIME_SHULKER_BOX.getLootTable(),
            Blocks.PINK_SHULKER_BOX.getLootTable(),
            Blocks.GRAY_SHULKER_BOX.getLootTable(),
            Blocks.LIGHT_GRAY_SHULKER_BOX.getLootTable(),
            Blocks.CYAN_SHULKER_BOX.getLootTable(),
            Blocks.PURPLE_SHULKER_BOX.getLootTable(),
            Blocks.BLUE_SHULKER_BOX.getLootTable(),
            Blocks.BROWN_SHULKER_BOX.getLootTable(),
            Blocks.GREEN_SHULKER_BOX.getLootTable(),
            Blocks.RED_SHULKER_BOX.getLootTable(),
            Blocks.BLACK_SHULKER_BOX.getLootTable()
    ).map(Optional::orElseThrow).collect(Collectors.toSet());

    public LootRandomiser(MinecraftServer server) {
        lootTableRegistry = (Registry<LootTable>) server.reloadableRegistries()
                .lookup()
                .lookupOrThrow(Registries.LOOT_TABLE);
        Set<ResourceKey<LootTable>> keys = lootTableRegistry.registryKeySet();
        keys.forEach(key -> {
            LootTable lootTable = lootTableRegistry.getValue(key);
            originalLootTables.put(key, lootTable);
        });
    }

    public static Codec<LootRandomiser> codec(MinecraftServer server) {
        return RecordCodecBuilder.create(instance -> instance.group(
                LootTableTracker.CODEC.listOf().fieldOf("trackers").forGetter(LootRandomiser::getTrackerList)
        ).apply(instance, (trackers) -> {
            LootRandomiser randomiser = new LootRandomiser(server);
            Map<Team, LootTableTracker> trackerMap = randomiser.getTrackers();
            trackerMap.clear();
            for (LootTableTracker tracker : trackers) {
                trackerMap.put(tracker.getTeam(), tracker);
                tracker.setLootTableRegistry(server.reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE));
            }
            return randomiser;
        }));
    }

    public ResourceKey<LootTable> getLootTable(@NotNull List<Team> teams, ResourceKey<LootTable> key) {
        ResourceKey<LootTable> result = redirectMap.getOrDefault(key, key);
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

    public ResourceKey<LootTable> getSourceTable(ResourceKey<LootTable> key) {
        return redirectMap.inverse().getOrDefault(key, key);
    }

    @Override
    public void randomise(MinecraftServer server, long seed) {
        redirectMap.clear();
        List<ResourceKey<LootTable>> keys = new ArrayList<>(originalLootTables.keySet());
        keys.sort(Comparator.comparing(ResourceKey::identifier));
        Random random = new Random(seed);
        Collections.shuffle(keys, random);
        UnaryBiMap<ResourceKey<LootTable>> preRedirects = new UnaryHashBiMap<>(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            ResourceKey<LootTable> originalKey = keys.get(i);
            ResourceKey<LootTable> randomKey = keys.get((i + 1) % keys.size());
            preRedirects.put(originalKey, randomKey);
        }
        blacklist.forEach(key -> {
            ResourceKey<LootTable> blacklistedDrops = preRedirects.remove(key);
            ResourceKey<LootTable> dropsBlacklisted = preRedirects.inverse().remove(key);
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
    public Map<Team, LootTableTracker> getTrackers() {
        return trackers;
    }

    @Override
    public List<LootTableTracker> getTrackerList() {
        return new ArrayList<>(trackers.values());
    }

    @Override
    public Module getModule() {
        return Module.LOOT_TABLES;
    }
}
