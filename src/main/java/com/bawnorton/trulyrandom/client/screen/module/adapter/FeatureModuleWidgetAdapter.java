package com.bawnorton.trulyrandom.client.screen.module.adapter;

import com.bawnorton.trulyrandom.client.screen.module.ModuleWidgetSettings;
import com.bawnorton.trulyrandom.random.module.Module;
import net.minecraft.client.gui.layouts.GridLayout;

public class FeatureModuleWidgetAdapter extends ModuleWidgetAdapter {
    @Override
    public GridLayout create(ModuleWidgetSettings settings, Module module) {
        GridLayout gridWidget = new GridLayout();
        GridLayout.RowHelper rowHelper = gridWidget.createRowHelper(1);
        rowHelper.addChild(
                settings.createBuilder(module, ModuleWidgetSettings.Title::new)
                        .dimensions(0, 0, 130, 17)
                        .build()
        );
        return gridWidget;
    }
}
