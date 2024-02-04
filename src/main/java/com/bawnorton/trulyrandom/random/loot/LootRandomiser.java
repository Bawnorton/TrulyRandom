package com.bawnorton.trulyrandom.random.loot;

import com.bawnorton.trulyrandom.mixin.accessor.LootManagerAccessor;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import java.util.*;

public class LootRandomiser extends ServerRandomiserModule {
    private final Map<LootDataKey<?>, Object> originalLootTables;
    private final Set<Identifier> randomiseBlacklist = new HashSet<>();

    public LootRandomiser(MinecraftServer server) {
        originalLootTables = ((LootManagerAccessor) server.getLootManager()).getKeyToValue();
    }

    public void randomise(MinecraftServer server, long seed) {
        readBlacklist();

        Map<LootDataKey<?>, Object> modifiableOriginalLootTables = new HashMap<>(this.originalLootTables);
        List<Map.Entry<LootDataKey<?>, Object>> ignored = new ArrayList<>();
        for (Map.Entry<LootDataKey<?>, Object> lootDataKeyObjectEntry : modifiableOriginalLootTables.entrySet()) {
            if (randomiseBlacklist.contains(lootDataKeyObjectEntry.getKey().id())) {
                ignored.add(lootDataKeyObjectEntry);
            }
        }
        Map<LootDataKey<?>, Object> unrandomisedLootTables = new HashMap<>();
        for (Map.Entry<LootDataKey<?>, Object> entry : ignored) {
            unrandomisedLootTables.put(entry.getKey(), entry.getValue());
            modifiableOriginalLootTables.remove(entry.getKey());
        }

        List<LootDataKey<?>> sortedLootTableKeys = new ArrayList<>(modifiableOriginalLootTables.keySet());
        sortedLootTableKeys.sort(Comparator.comparing(LootDataKey::id));

        List<Object> randomLootTables = new ArrayList<>(modifiableOriginalLootTables.values());
        Collections.shuffle(randomLootTables, new Random(seed));

        Map<LootDataKey<?>, Object> randomisedLootTables = new HashMap<>();
        for (int i = 0; i < sortedLootTableKeys.size(); i++) {
            randomisedLootTables.put(sortedLootTableKeys.get(i), randomLootTables.get(i));
        }
        randomisedLootTables.putAll(unrandomisedLootTables);
        ((LootManagerAccessor) server.getLootManager()).setKeyToValue(randomisedLootTables);
        setRandomised(true);
    }

    private void readBlacklist() {
        randomiseBlacklist.clear();
        Registries.BLOCK.streamEntries()
                        .filter(entry -> entry.isIn(BlockTags.SHULKER_BOXES))
                        .forEach(entry -> randomiseBlacklist.add(entry.value().getLootTableId()));
        originalLootTables.entrySet().stream()
                          .filter(entry -> !(entry.getValue() instanceof LootTable))
                          .map(entry -> entry.getKey().id())
                          .forEach(randomiseBlacklist::add);
    }

    public void reset(MinecraftServer server) {
        LootManager lootManager = server.getLootManager();
        LootManagerAccessor lootManagerAccessor = (LootManagerAccessor) lootManager;
        lootManagerAccessor.setKeyToValue(originalLootTables);
        setRandomised(false);
    }

    @Override
    public Module getModule() {
        return Module.LOOT_TABLES;
    }
}
