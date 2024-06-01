package com.bawnorton.trulyrandom.random.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.collection.UnaryMap;
import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.mojang.serialization.DataResult;
import net.minecraft.entity.player.PlayerEntity;
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

    public LootRandomiser(MinecraftServer server) {
        Registry<LootTable> lootTableRegistry = server.getReloadableRegistries()
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
            teams.forEach(team -> trackers.computeIfAbsent(team, k -> {
                        LootTableTracker tracker = new LootTableTracker();
                        tracker.setTeam(team);
                        return tracker;
                    }).track(key, result));
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
            result.result().ifPresent(tracker -> trackers.put(tracker.getTeam(), tracker));
            result.error().ifPresent(e -> TrulyRandom.LOGGER.error(e.message()));
        }
    }

    @Override
    public void randomise(MinecraftServer server, long seed) {
        List<RegistryKey<LootTable>> keys = new ArrayList<>(originalLootTables.keySet());
        keys.sort(Comparator.comparing(RegistryKey::getValue));
        Random random = new Random(seed);
        Collections.shuffle(keys, random);
        for (int i = 0; i < keys.size(); i++) {
            RegistryKey<LootTable> originalKey = keys.get(i);
            RegistryKey<LootTable> randomKey = keys.get((i + 1) % keys.size());
            redirectMap.put(originalKey, randomKey);
        }
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
