package com.bawnorton.trulyrandom.random.trade;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import com.bawnorton.trulyrandom.team.Team;
import com.bawnorton.trulyrandom.tracker.trade.TradeTracker;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.Item;

import java.util.*;

public class TradeRandomiser extends ServerRandomiserModule {
    private final Map<UUID, TradeTracker> trackers = new HashMap<>();
    private final UnaryMap<Item> redirectMap = new UnaryHashMap<>();
    private final Map<Item, Integer> countMap = new HashMap<>();

    public Item getItem(AbstractVillager villager, Item key) {
        Random subRandom = new Random(villager.getUUID().hashCode());
        int passes = subRandom.nextInt(redirectMap.size() / 2);
        Item result = redirectMap.getOrDefault(key, key);
        while (passes-- > 0) {
            result = redirectMap.getOrDefault(result, key);
        }
        return result;
    }

    public int getCount(AbstractVillager villager, Item key, int defaultCount) {
        Random subRandom = new Random(villager.getUUID().hashCode());
        int count = countMap.getOrDefault(key, defaultCount);
        count += subRandom.nextInt(key.getDefaultMaxStackSize());
        while (count > key.getDefaultMaxStackSize()) {
            count -= key.getDefaultMaxStackSize();
        }
        count /= subRandom.nextInt(1, 10);
        if (count == 0) count = 1;
        return count;
    }

    public void track(AbstractVillager villager, Team team, Item key) {
        Item result = getItem(villager, key);
        if(!result.equals(key)) {
            trackers.computeIfAbsent(team.getOwner(), _ -> {
                TradeTracker tracker = new TradeTracker();
                tracker.setTeam(team);
                return tracker;
            }).track(key, result);
        }
    }

    @Override
    public void randomise(MinecraftServer server, long seed) {
        List<Item> keys = new ArrayList<>(BuiltInRegistries.ITEM.stream().toList());
        Random rand = new Random(seed);
        Collections.shuffle(keys, rand);
        for (int i = 0; i < keys.size(); i++) {
            Item key = keys.get(i);
            Item next = keys.get((i + 1) % keys.size());
            redirectMap.put(key, next);
            countMap.put(key, rand.nextInt(1, 65));
        }
    }

    @Override
    public void reset(MinecraftServer server) {
        redirectMap.clear();
        countMap.clear();
    }

    @Override
    public TradeTracker getTracker(Team team) {
        return trackers.get(team.getOwner());
    }

    @Override
    public List<TradeTracker> getTrackerList() {
        return new ArrayList<>(trackers.values());
    }

    @Override
    public Map<UUID, TradeTracker> getTrackers() {
        return trackers;
    }

    @Override
    public Module getModule() {
        return Module.TRADES;
    }
}
