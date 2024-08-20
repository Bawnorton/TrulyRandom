package com.bawnorton.trulyrandom.tracker.trade;

import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

public class TradeTracker extends Tracker<Item, Item> {
    public static final PacketCodec<RegistryByteBuf, TradeTracker> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(
                    HashMap::new,
                    Identifier.PACKET_CODEC.xmap(Registries.ITEM::get, Registries.ITEM::getId),
                    Identifier.PACKET_CODEC.xmap(Registries.ITEM::get, Registries.ITEM::getId)
            ), tracker -> tracker.knownTrades,
            Team.PACKET_CODEC, tracker -> tracker.team,
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