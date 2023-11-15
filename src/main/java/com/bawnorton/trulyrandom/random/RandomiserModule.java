package com.bawnorton.trulyrandom.random;

public abstract class RandomiserModule {
    private boolean randomised = false;

    public boolean isRandomised() {
        return randomised;
    }

    public void setRandomised(boolean randomised) {
        this.randomised = randomised;
    }
}
