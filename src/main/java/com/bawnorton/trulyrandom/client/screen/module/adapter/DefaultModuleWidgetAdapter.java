package com.bawnorton.trulyrandom.client.screen.module.adapter;

import com.bawnorton.trulyrandom.client.screen.module.ModuleWidgetSettings;
import com.bawnorton.trulyrandom.random.module.Module;
import net.minecraft.client.gui.layouts.GridLayout;

public class DefaultModuleWidgetAdapter extends ModuleWidgetAdapter {
    @Override
    public GridLayout create(ModuleWidgetSettings settings, Module module) {
        GridLayout gridWidget = new GridLayout();
        GridLayout.RowHelper rowHelper = gridWidget.createRowHelper(1);
        ModuleWidgetSettings.Title title = rowHelper.addChild(
                settings.createBuilder(module, ModuleWidgetSettings.Title::new)
                        .dimensions(0, 0, 130, 17)
                        .build()
        );
        rowHelper.addChild(
                settings.createBuilder(module, ModuleWidgetSettings.SeedBox::new)
                        .dimensions(0, title.getHeight(), 130, 17)
                        .build()
        );
        gridWidget.rowSpacing(2);
        return gridWidget;
    }
}
