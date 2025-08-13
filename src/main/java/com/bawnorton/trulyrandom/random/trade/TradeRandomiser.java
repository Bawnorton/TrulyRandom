package com.bawnorton.trulyrandom.random.trade;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.trade.TradeTracker;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TradeRandomiser extends ServerRandomiserModule {
    private final Map<Team, TradeTracker> trackers = new HashMap<>();
    private final UnaryMap<Item> redirectMap = new UnaryHashMap<>();
    private final Map<Item, Integer> countMap = new HashMap<>();

    public Item getItem(MerchantEntity merchantEntity, Item key) {
        Random subRandom = new Random(merchantEntity.getUuid().hashCode());
        int passes = subRandom.nextInt(redirectMap.size() / 2);
        Item result = redirectMap.getOrDefault(key, key);
        while (passes-- > 0) {
            result = redirectMap.getOrDefault(result, key);
        }
        return result;
    }

    public int getCount(MerchantEntity merchantEntity, Item key, int defaultCount) {
        Random subRandom = new Random(merchantEntity.getUuid().hashCode());
        int count = countMap.getOrDefault(key, defaultCount);
        count += subRandom.nextInt(key.getMaxCount());
        while (count > key.getMaxCount()) {
            count -= key.getMaxCount();
        }
        count /= subRandom.nextInt(1, 10);
        if (count == 0) count = 1;
        return count;
    }

    public void track(MerchantEntity merchantEntity, Team team, Item key) {
        Item result = getItem(merchantEntity, key);
        if(!result.equals(key)) {
            trackers.computeIfAbsent(team, k -> {
                TradeTracker tracker = new TradeTracker();
                tracker.setTeam(team);
                return tracker;
            }).track(key, result);
        }
    }

    @Override
    public void randomise(MinecraftServer server, long seed) {
        List<Item> keys = new ArrayList<>(Registries.ITEM.stream().toList());
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
    public TradeTracker getTracker(TeamMember teamMember) {
        return trackers.get(teamMember.trulyrandom$getTeam());
    }

    @Override
    public List<TradeTracker> getTrackerList() {
        return new ArrayList<>(trackers.values());
    }

    @Override
    public Map<Team, TradeTracker> getTrackers() {
        return trackers;
    }

    @Override
    public Module getModule() {
        return Module.TRADES;
    }
}
