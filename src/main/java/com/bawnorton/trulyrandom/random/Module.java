package com.bawnorton.trulyrandom.random;

public enum Module {
    LOOT_TABLES(false),
    RECIPES(false),
    STRUCTURES(false),
    BLOCK_MODELS(true),
    ITEM_MODELS(true);

    private final boolean implemented;

    private boolean enabled;

    Module(boolean implemented) {
        this.implemented = implemented;
    }

    public boolean isImplemented() {
        return implemented;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
