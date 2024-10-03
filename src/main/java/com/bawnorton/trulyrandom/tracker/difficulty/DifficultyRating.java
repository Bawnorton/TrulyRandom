package com.bawnorton.trulyrandom.tracker.difficulty;

public enum DifficultyRating {
    UNKNOWN,
    VANILLA,
    TRIVIAL,
    EASY,
    MEDIUM,
    HARD,
    EXTREME,
    IMPOSSIBLE;

    public static DifficultyRating eaiser(DifficultyRating rating1, DifficultyRating rating2) {
        if(rating1.easierThan(rating2)) {
            return rating1;
        } else {
            return rating2;
        }
    }

    public boolean easierThan(DifficultyRating difficultyRating) {
        return ordinal() < difficultyRating.ordinal();
    }

    public boolean harderThan(DifficultyRating difficultyRating) {
        return ordinal() > difficultyRating.ordinal();
    }
}
