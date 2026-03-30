package com.bawnorton.trulyrandom.tracker.loot;

import joptsimple.internal.Strings;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;

public class LootTableIdentifier {
    private final String namespace;
    private final String[] segments;

    public LootTableIdentifier(Identifier lootTableId) {
        this.namespace = lootTableId.getNamespace();
        this.segments = lootTableId.getPath().split("/");
    }

    public String getNamespace() {
        return namespace;
    }

    public String[] getSegments() {
        return segments;
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

    public boolean isReward() {
        return isFromChest() && segmentStartsWith(2, "reward");
    }

    public boolean isOminous() {
        return isReward() && segmentContains(2, "ominous");
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

    public boolean isChickenLay() {
        return isFromGameplay() && isSegment(1, "chicken_lay");
    }

    public boolean isArmadilloShed() {
        return isFromGameplay() && isSegment(1, "armadillo_shed");
    }

    public boolean isTurtleGrow() {
        return isFromGameplay() && isSegment(1, "turtle_grow");
    }

    public boolean isFromPot() {
        return isFrom("pots");
    }

    public boolean isFromShearing() {
        return isFrom("shearing");
    }

    public boolean isBoggedShearing() {
        return isFromShearing() && isSegment(1, "bogged");
    }

    public boolean isMooshroomShearing() {
        return isFromShearing() && isSegment(1, "mooshroom");
    }

    public boolean isRedMooshroomShearing() {
        return isMooshroomShearing() && isSegment(2, "red");
    }

    public boolean isBrownMooshroomShearing() {
        return isMooshroomShearing() && isSegment(2, "brown");
    }

    public boolean isSnowGolemShearing() {
        return isFromShearing() && isSegment(1, "snow_golem");
    }

    public boolean isSheepShearing() {
        return isFromShearing() && isSegment(1, "sheep");
    }

    public boolean isColouredSheepShearing() {
        return isSheepShearing() && segments.length == 3;
    }

    public DyeColor getDyeColourForSheepShearing() {
        if(isColouredSheepShearing()) {
            String segment = segments[2];
            return DyeColor.byName(segment, DyeColor.WHITE);
        }
        return DyeColor.WHITE;
    }

    public boolean isFromSpawner() {
        return isFrom("spawners");
    }

    public boolean isFromBrush() {
        return isFrom("brush");
    }

    public boolean isArmadilloBrushing() {
        return isFromBrush() && isSegment(1, "armadillo");
    }

    public boolean isEmpty() {
        return isFrom("empty");
    }

    private boolean isFrom(String key) {
        if(segments.length == 0) return false;

        return segments[0].equals(key);
    }

    private boolean isSegment(int index, String value) {
        if(index >= segments.length) return false;

        return segments[index].equals(value);
    }

    private boolean segmentStartsWith(int index, String start) {
        if (segments.length <= index) return false;

        return segments[index].startsWith(start);
    }

    private boolean segmentContains(int index, String content) {
        if (segments.length <= index) return false;

        return segments[index].contains(content);
    }

    public Identifier getSourceId() {
        if(isFromBlock() || isFromEntity()) {
            return Identifier.fromNamespaceAndPath(namespace, segments[1]);
        }
        return null;
    }

    @Override
    public String toString() {
        return "%s:%s".formatted(namespace, Strings.join(segments, "/"));
    }
}
