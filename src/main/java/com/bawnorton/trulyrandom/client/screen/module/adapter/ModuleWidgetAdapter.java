package com.bawnorton.trulyrandom.client.screen.module.adapter;

import com.bawnorton.trulyrandom.client.screen.module.settings.ModuleWidgetSettings;
import com.bawnorton.trulyrandom.random.module.Module;
import net.minecraft.client.gui.layouts.GridLayout;

public abstract class ModuleWidgetAdapter {
    protected static final int DEFAULT_WIDTH = 160;

    public abstract GridLayout create(ModuleWidgetSettings settings, Module module);
}
