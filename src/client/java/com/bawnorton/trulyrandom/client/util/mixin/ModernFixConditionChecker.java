package com.bawnorton.trulyrandom.client.util.mixin;

import com.bawnorton.trulyrandom.client.TrulyRandomMixinConfigPlugin;
import com.bawnorton.trulyrandom.client.compat.ModernFixCompat;

public class ModernFixConditionChecker implements AdvancedConditionChecker {
    @Override
    public boolean shouldApply() {
        return TrulyRandomMixinConfigPlugin.isModLoaded("modernfix") && ModernFixCompat.isDynamicResourcesEnabled();
    }
}
