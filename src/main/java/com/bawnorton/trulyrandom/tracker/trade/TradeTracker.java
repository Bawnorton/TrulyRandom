package com.bawnorton.trulyrandom.tracker.trade;

import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public class TradeTracker extends Tracker<Item, Item> {
    public static final StreamCodec<RegistryFriendlyByteBuf, TradeTracker> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    Identifier.STREAM_CODEC.map(BuiltInRegistries.ITEM::getValue, BuiltInRegistries.ITEM::getKey),
                    Identifier.STREAM_CODEC.map(BuiltInRegistries.ITEM::getValue, BuiltInRegistries.ITEM::getKey)
            ), tracker -> tracker.knownTrades,
            Team.STREAM_CODEC, tracker -> tracker.team,
            TradeTracker::new
    );

    private final UnaryMap<Item> knownTrades;

    public TradeTracker(Map<Item, Item> map, Team team) {
        super(team);
        this.knownTrades = new UnaryHashMap<>(map);
    }

    public TradeTracker() {
        super(null);
        this.knownTrades = new UnaryHashMap<>();
    }

    @Override
    public void track(Item from, Item to) {
        knownTrades.put(from, to);
        this.markDirty();
    }

    @Override
    public Map<Item, Item> known() {
        return knownTrades;
    }

    @Override
    public void reset() {
        knownTrades.clear();
        this.markDirty();
    }
}