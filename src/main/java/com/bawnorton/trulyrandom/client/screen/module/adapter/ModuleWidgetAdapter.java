package com.bawnorton.trulyrandom.client.screen.module.adapter;

import com.bawnorton.trulyrandom.client.screen.module.ModuleWidgetSettings;
import com.bawnorton.trulyrandom.random.module.Module;
import net.minecraft.client.gui.layouts.GridLayout;

public abstract class ModuleWidgetAdapter {
    public abstract GridLayout create(ModuleWidgetSettings settings, Module module);
}
