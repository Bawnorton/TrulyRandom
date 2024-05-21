package com.bawnorton.trulyrandom.random.loot;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import java.util.*;

public class LootRandomiser extends ServerRandomiserModule {
    private final Map<RegistryKey<LootTable>, LootTable> originalLootTables = new HashMap<>();
    private final Map<RegistryKey<LootTable>, RegistryKey<LootTable>> redirectMap = new HashMap<>();

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

    public RegistryKey<LootTable> getLootTable(RegistryKey<LootTable> key) {
        return redirectMap.getOrDefault(key, key);
    }

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

    public void reset(MinecraftServer server) {
        redirectMap.clear();
    }

    @Override
    public Module getModule() {
        return Module.LOOT_TABLES;
    }
}
