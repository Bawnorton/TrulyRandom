package com.bawnorton.trulyrandom.random.loot;

import com.bawnorton.trulyrandom.mixin.accessor.LootManagerAccessor;
import com.bawnorton.trulyrandom.random.Module;
import com.bawnorton.trulyrandom.random.ServerRandomiserModule;
import net.minecraft.block.Block;
import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.*;

public class LootRandomiser extends ServerRandomiserModule {
    private final Map<LootDataKey<?>, LootTable> originalLootTables;
    private final Set<Identifier> randomiseBlacklist = new HashSet<>();

    @SuppressWarnings("unchecked")
    public LootRandomiser(MinecraftServer server) {
        originalLootTables = (Map<LootDataKey<?>, LootTable>) ((LootManagerAccessor) server.getLootManager()).getKeyToValue();
    }

    public void randomise(MinecraftServer server, long seed) {
        List<LootDataKey<?>> keys = originalLootTables.keySet().stream().sorted(Comparator.comparing(LootDataKey::id)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        List<LootTable> values = keys.stream().map(originalLootTables::get).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        Collections.shuffle(values, new Random(seed));
        Map<LootDataKey<?>, LootTable> newKeyToValue = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            newKeyToValue.put(keys.get(i), values.get(i));
        }
        Map<Identifier, Pair<LootDataKey<?>, LootTable>> idToLootTable = new HashMap<>();
        originalLootTables.forEach((key, value) -> idToLootTable.put(key.id(), new Pair<>(key, value)));
        readBlacklist();
        for (Identifier id : randomiseBlacklist) {
            newKeyToValue.put(idToLootTable.get(id).getLeft(), idToLootTable.get(id).getRight());
        }
        ((LootManagerAccessor) server.getLootManager()).setKeyToValue(newKeyToValue);
        setRandomised(true);
    }

    private void readBlacklist() {
        randomiseBlacklist.clear();
        Registries.BLOCK.stream().map(Block::getDefaultState).filter(state -> state.isIn(BlockTags.SHULKER_BOXES)).forEach(state -> randomiseBlacklist.add(state.getBlock().getLootTableId()));
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
