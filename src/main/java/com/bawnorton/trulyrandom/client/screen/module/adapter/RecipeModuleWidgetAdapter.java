package com.bawnorton.trulyrandom.client.screen.module.adapter;

import com.bawnorton.trulyrandom.client.screen.module.ModuleWidgetSettings;
import com.bawnorton.trulyrandom.client.screen.module.RecipeModuleSettings;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.state.RecipeModuleState;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

public class RecipeModuleWidgetAdapter extends ModuleWidgetAdapter {
    @Override
    public GridLayout create(ModuleWidgetSettings settings, Module module) {
        GridLayout gridWidget = new GridLayout();
        GridLayout.RowHelper rowHelper = gridWidget.createRowHelper(1);
        ModuleWidgetSettings.Title title = rowHelper.addChild(
                settings.createBuilder(module, RecipeTitle::new)
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

    public static class RecipeTitle extends ModuleWidgetSettings.Title {
        private Button settingsButton;

        public RecipeTitle(ModuleWidgetSettings settings, Module module, int x, int y, int width, int height, int columns) {
            super(settings, module, x, y, width, height, 3);
        }

        @Override
        protected void addTitle(int x, int y, int width, int height) {
            super.addTitle(x, y, width - 58, height);
        }

        @Override
        protected void addToggle(int x, int y, int width, int height) {
            settingsButton = Button.builder(Component.translatable("selectWorld.trulyrandom.settings"), _ -> minecraft.setScreen(new RecipeModuleSettings(Component.translatable("selectWorld.trulyrandom"), minecraft.screen, modules.getState(module, RecipeModuleState.class))))
                    .bounds(x, y, 56, height)
                    .build();
            super.addToggle(x, y, width, height);
        }

        @Override
        protected void addElements(RowHelper rowHelper) {
            rowHelper.addChild(title, 1);
            rowHelper.addChild(settingsButton, 1);
            rowHelper.addChild(toggleButton, 1);
        }
    }
}
