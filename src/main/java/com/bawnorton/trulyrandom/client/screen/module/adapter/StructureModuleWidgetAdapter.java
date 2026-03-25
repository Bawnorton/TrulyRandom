package com.bawnorton.trulyrandom.client.screen.module.adapter;

import com.bawnorton.trulyrandom.client.screen.module.ModuleWidgetSettings;
import com.bawnorton.trulyrandom.random.module.Module;
import net.minecraft.client.gui.widget.GridWidget;

public class StructureModuleWidgetAdapter extends ModuleWidgetAdapter {
    @Override
    public GridWidget create(ModuleWidgetSettings settings, Module module) {
        GridWidget gridWidget = new GridWidget();
        GridWidget.Adder adder = gridWidget.createAdder(1);
        adder.add(
                settings.createBuilder(module, ModuleWidgetSettings.Title::new)
                        .dimensions(0, 0, 130, 17)
                        .build()
        );
        return gridWidget;
    }


}
