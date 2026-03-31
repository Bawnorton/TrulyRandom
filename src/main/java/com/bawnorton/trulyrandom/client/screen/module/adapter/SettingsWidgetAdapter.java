package com.bawnorton.trulyrandom.client.screen.module.adapter;

import com.bawnorton.trulyrandom.client.screen.module.ModuleWidgetSettings;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.state.ModuleState;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.function.TriFunction;

public class SettingsWidgetAdapter<T extends ModuleState> extends ModuleWidgetAdapter {
    private final Class<T> moduleStateClass;
    private final TriFunction<Component, Screen, T, Screen> settingsScreenFactory;

    public SettingsWidgetAdapter(Class<T> moduleStateClass, TriFunction<Component, Screen, T, Screen> settingsScreenFactory) {
        this.moduleStateClass = moduleStateClass;
        this.settingsScreenFactory = settingsScreenFactory;
    }

    @Override
    public GridLayout create(ModuleWidgetSettings settings, Module module) {
        GridLayout gridWidget = new GridLayout();
        GridLayout.RowHelper rowHelper = gridWidget.createRowHelper(1);
        ModuleWidgetSettings.Title title = rowHelper.addChild(
                settings.createBuilder(module, SettingsWidgetAdapter.Title::new)
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

    public class Title extends ModuleWidgetSettings.Title {
        private Button settingsButton;

        public Title(ModuleWidgetSettings settings, Module module, int x, int y, int width, int height, int columns) {
            super(settings, module, x, y, width, height, 3);
        }

        @Override
        protected void addTitle(int x, int y, int width, int height) {
            super.addTitle(x, y, width - 58, height);
        }

        @Override
        protected void addToggle(int x, int y, int width, int height) {
            settingsButton = Button.builder(Component.translatable("selectWorld.trulyrandom.settings"), _ -> minecraft.setScreen(settingsScreenFactory.apply(Component.translatable("selectWorld.trulyrandom"), minecraft.screen, modules.getState(module, moduleStateClass))))
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
