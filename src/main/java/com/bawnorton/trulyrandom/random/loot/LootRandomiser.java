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
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LootRandomiser extends ServerRandomiserModule {
    private final Map<Team, LootTableTracker> trackers = new HashMap<>();
    private final Map<RegistryKey<LootTable>, LootTable> originalLootTables = new HashMap<>();
    private final UnaryBiMap<RegistryKey<LootTable>> redirectMap = new UnaryHashBiMap<>();
    private final Registry<LootTable> lootTableRegistry;

    private final Set<RegistryKey<LootTable>> blacklist = Stream.of(
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
    ).map(Optional::orElseThrow).collect(Collectors.toSet());

    public LootRandomiser(MinecraftServer server) {
        lootTableRegistry = (Registry<LootTable>) server.getReloadableRegistries()
                .createRegistryLookup()
                .getOrThrow(RegistryKeys.LOOT_TABLE);
        Set<RegistryKey<LootTable>> keys = lootTableRegistry.getKeys();
        keys.forEach(key -> {
            LootTable lootTable = lootTableRegistry.get(key);
            originalLootTables.put(key, lootTable);
        });
    }

    public static Codec<LootRandomiser> codec(PersistentState.Context context) {
        ServerWorld world = context.getWorldOrThrow();
        MinecraftServer server = world.getServer();
        return RecordCodecBuilder.create(instance -> instance.group(
                LootTableTracker.CODEC.listOf().fieldOf("trackers").forGetter(LootRandomiser::getTrackerList)
        ).apply(instance, (trackers) -> {
            LootRandomiser randomiser = new LootRandomiser(server);
            Map<Team, LootTableTracker> trackerMap = randomiser.getTrackers();
            trackerMap.clear();
            for (LootTableTracker tracker : trackers) {
                trackerMap.put(tracker.getTeam(), tracker);
                tracker.setLootTableRegistry(server.getReloadableRegistries().createRegistryLookup().getOrThrow(RegistryKeys.LOOT_TABLE));
            }
            return randomiser;
        }));
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

    public RegistryKey<LootTable> getSourceTable(RegistryKey<LootTable> key) {
        return redirectMap.inverse().getOrDefault(key, key);
    }

    @Override
    public void randomise(MinecraftServer server, long seed) {
        redirectMap.clear();
        List<RegistryKey<LootTable>> keys = new ArrayList<>(originalLootTables.keySet());
        keys.sort(Comparator.comparing(RegistryKey::getValue));
        Random random = new Random(seed);
        Collections.shuffle(keys, random);
        UnaryBiMap<RegistryKey<LootTable>> preRedirects = new UnaryHashBiMap<>(keys.size());
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
