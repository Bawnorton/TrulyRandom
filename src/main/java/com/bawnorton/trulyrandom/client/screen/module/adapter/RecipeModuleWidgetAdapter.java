package com.bawnorton.trulyrandom.client.screen.module.adapter;

import com.bawnorton.trulyrandom.client.screen.module.ModuleWidgetSettings;
import com.bawnorton.trulyrandom.client.screen.module.RecipeModuleSettings;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.RecipeModuleState;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;

public class RecipeModuleWidgetAdapter extends ModuleWidgetAdapter {
    @Override
    public GridWidget create(ModuleWidgetSettings settings, Module module) {
        GridWidget gridWidget = new GridWidget();
        GridWidget.Adder adder = gridWidget.createAdder(1);
        ModuleWidgetSettings.Title title = adder.add(
                settings.createBuilder(module, RecipeTitle::new)
                        .dimensions(0, 0, 130, 17)
                        .build()
        );
        adder.add(
                settings.createBuilder(module, ModuleWidgetSettings.SeedBox::new)
                        .dimensions(0, title.getHeight(), 130, 17)
                        .build()
        );
        gridWidget.setRowSpacing(2);
        return gridWidget;
    }

    public static class RecipeTitle extends ModuleWidgetSettings.Title {
        private ButtonWidget settingsButton;

        public RecipeTitle(ModuleWidgetSettings settings, Module module, int x, int y, int width, int height, int columns) {
            super(settings, module, x, y, width, height, 3);
        }

        @Override
        protected void addTitle(int x, int y, int width, int height) {
            super.addTitle(x, y, width - 58, height);
        }

        @Override
        protected void addToggle(int x, int y, int width, int height) {
            settingsButton = ButtonWidget.builder(Text.translatable("selectWorld.trulyrandom.settings"), button -> client.setScreen(new RecipeModuleSettings(Text.translatable("selectWorld.trulyrandom"), client.currentScreen, modules.getState(module, RecipeModuleState.class))))
                    .dimensions(x, y, 56, height)
                    .build();
            super.addToggle(x, y, width, height);
        }

        @Override
        protected void addElements(Adder adder) {
            adder.add(title, 1);
            adder.add(settingsButton, 1);
            adder.add(toggleButton, 1);
        }
    }
}
