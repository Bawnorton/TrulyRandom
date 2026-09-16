package com.bawnorton.trulyrandom.team;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.TriState;

public class TeamActionResult {
    public static final TeamActionResult SUCCESS = positive("trulyrandom.team.success");
    public static final TeamActionResult IS_OWNER = neutral("trulyrandom.team.owner");
    public static final TeamActionResult IS_ALREADY_OWNER = neutral("trulyrandom.team.already_owner");
    public static final TeamActionResult IS_NOT_A_MEMBER = negative("trulyrandom.team.not_a_member");
    public static final TeamActionResult IS_BANNED = negative("trulyrandom.team.banned");
    public static final TeamActionResult IS_NOT_INVITED = negative("trulyrandom.team.not_invited");
    public static final TeamActionResult ALREADY_BANNED = neutral("trulyrandom.team.already_banned");
    public static final TeamActionResult ALREADY_INVITED = neutral("trulyrandom.team.already_invited");
    public static final TeamActionResult ALREADY_IN_TEAM = neutral("trulyrandom.team.already_in_team");
    public static final TeamActionResult IS_NOT_BANNED = neutral("trulyrandom.team.is_not_banned");
    public static final TeamActionResult IS_NOT_OWNER = negative("trulyrandom.team.not_owner");

    private final String translationKey;
    private final TriState state;

    private TeamActionResult(String translationKey, TriState state) {
        this.translationKey = translationKey;
        this.state = state;
    }

    public TriState getState() {
        return state;
    }

    public boolean isPositive() {
        return state == TriState.TRUE;
    }

    public Component getReasonText(Object... args) {
        return Component.translatable(translationKey, args).withStyle(switch (state) {
            case TRUE -> ChatFormatting.GREEN;
            case FALSE -> ChatFormatting.RED;
            case DEFAULT -> ChatFormatting.GOLD;
        });
    }

    public TeamActionResult asPositive() {
        if(getState() == TriState.TRUE) return this;

        return positive(translationKey);
    }

    public TeamActionResult asNegative() {
        if(getState() == TriState.FALSE) return this;

        return negative(translationKey);
    }

    public TeamActionResult asNeutral() {
        if(getState() == TriState.DEFAULT) return this;

        return neutral(translationKey);
    }

    public TeamActionResult withVariant(String variant) {
        return new TeamActionResult("%s.%s".formatted(translationKey, variant), state);
    }

    public static TeamActionResult positive(String translationKey) {
        return new TeamActionResult(translationKey, TriState.TRUE);
    }

    public static TeamActionResult negative(String translationKey) {
        return new TeamActionResult(translationKey, TriState.FALSE);
    }

    public static TeamActionResult neutral(String translationKey) {
        return new TeamActionResult(translationKey, TriState.DEFAULT);
    }
}
