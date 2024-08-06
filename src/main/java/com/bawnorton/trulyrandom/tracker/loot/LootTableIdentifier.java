package com.bawnorton.trulyrandom.tracker.loot;

import joptsimple.internal.Strings;
import net.minecraft.util.Identifier;

public class LootTableIdentifier {
    private final String namespace;
    private final String[] segments;

    public LootTableIdentifier(Identifier lootTableId) {
        this.namespace = lootTableId.getNamespace();
        this.segments = lootTableId.getPath().split("/");
    }

    public static LootTableIdentifier from(Identifier lootTableId) {
        return new LootTableIdentifier(lootTableId);
    }

    public boolean isFromArchaelogy() {
        return isFrom("archaeology");
    }

    public boolean isFromBlock() {
        return isFrom("blocks");
    }

    public boolean isFromChest() {
        return isFrom("chests");
    }

    public boolean isFromDispenser() {
        return isFrom("dispensers");
    }

    public boolean isFromEntity() {
        return isFrom("entities");
    }

    public boolean isFromEquipment() {
        return isFrom("equipment");
    }

    public boolean isFromGameplay() {
        return isFrom("gameplay");
    }

    public boolean isFishing() {
        return isFromGameplay() && isSegment(1, "fishing");
    }

    public boolean isCatMorningGift() {
        return isFromGameplay() && isSegment(1, "cat_morning_gift");
    }

    public boolean isPandaSneeze() {
        return isFromGameplay() && isSegment(1, "panda_sneeze");
    }

    public boolean isPiglinBartering() {
        return isFromGameplay() && isSegment(1, "piglin_bartering");
    }

    public boolean isSnifferDigging() {
        return isFromGameplay() && isSegment(1, "sniffer_digging");
    }

    public boolean isHeroOfTheVillage() {
        return isFromGameplay() && isSegment(1, "hero_of_the_village");
    }

    public boolean isFromPot() {
        return isFrom("pots");
    }

    public boolean isFromShearing() {
        return isFrom("shearing");
    }

    public boolean isFromSpawner() {
        return isFrom("spawners");
    }

    public boolean isEmpty() {
        return isFrom("empty");
    }

    private boolean isFrom(String key) {
        if(segments.length == 0) return false;

        return segments[0].equals(key);
    }

    private boolean isSegment(int index, String segment) {
        if(index >= segments.length) return false;

        return segments[index].equals(segment);
    }

    public Identifier getSourceId() {
        if(isFromBlock() || isFromEntity()) {
            return Identifier.of(namespace, segments[1]);
        }
        return null;
    }

    @Override
    public String toString() {
        return "LootTableIdentifier[%s:%s]".formatted(namespace, Strings.join(segments, "/"));
    }
}
