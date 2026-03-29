package com.bawnorton.trulyrandom.client.screen.module;

import com.bawnorton.trulyrandom.client.screen.module.adapter.ModuleWidgetAdapter;
import com.bawnorton.trulyrandom.client.screen.widget.LongEditBoxWidget;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.util.ModuleAdpatable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;

import java.util.Random;

public class ModuleWidgetSettings extends ModuleAdpatable<ModuleWidgetAdapter> {
    private final Minecraft minecraft;
    private final Modules modules;

    public ModuleWidgetSettings(Minecraft minecraft, Modules modules) {
        this.minecraft = minecraft;
        this.modules = modules;
    }

    public LayoutElement getWidget(Module module) {
        return getAdapter(module).create(this, module);
    }

    public <T extends ModuleElement> Builder<T> createBuilder(Module module, ModuleElementFactory<T> factory) {
        return new Builder<>(this, module, factory);
    }

    public abstract static class ModuleElement extends GridLayout {
        protected final Minecraft minecraft;
        protected final Modules modules;
        protected final Module module;
        private final int columns;

        protected ModuleElement(ModuleWidgetSettings settings, Module module, int columns) {
            this.minecraft = settings.minecraft;
            this.modules = settings.modules;
            this.module = module;
            this.columns = columns;
        }

        public void init() {
            addElements(createRowHelper(columns));
        }

        protected abstract void addElements(GridLayout.RowHelper adder);
    }

    public static class Title extends ModuleElement {
        protected StringWidget title;
        protected CycleButton<Boolean> toggleButton;

        public Title(ModuleWidgetSettings settings, Module module, int x, int y, int width, int height, int columns) {
            super(settings, module, columns);
            addTitle(x, y, width, height);
            addToggle(x, y, width, height);
        }

        protected void addTitle(int x, int y, int width, int height) {
            title = new StringWidget(x, y, width, height, Component.translatable("selectWorld.trulyrandom.%s".formatted(module.name().toLowerCase())), minecraft.font);
            title.setTooltip(Tooltip.create(getTooltipText()));
        }

        protected void addToggle(int x, int y, int width, int height) {
            toggleButton = CycleButton.onOffBuilder(modules.getEnabledMemento(module))
                    .displayOnlyValue()
                    .create(x, y, 44, height, Component.empty(), (_, value) -> modules.setEnabledMemento(module, value));
            toggleButton.active = module.isImplemented() && (minecraft.level == null || module.isMutable());
        }

        private Component getTooltipText() {
            if (module.isImplemented()) {
                return Component.translatable("selectWorld.trulyrandom.%s.tooltip".formatted(module.name().toLowerCase()));
            } else {
                return Component.translatable("selectWorld.trulyrandom.not_implemented")
                        .withStyle(ChatFormatting.DARK_GRAY)
                        .withStyle(ChatFormatting.ITALIC);
            }
        }

        @Override
        protected void addElements(RowHelper rowHelper) {
            rowHelper.addChild(title);
            rowHelper.addChild(toggleButton);
        }
    }

    public static class SeedBox extends ModuleElement {
        protected LongEditBoxWidget seedEditBox;
        protected Button newSeedButton;

        public SeedBox(ModuleWidgetSettings settings, Module module, int x, int y, int width, int height, int columns) {
            super(settings, module, columns);
            addSeedBox(module, x, y, width, height);
            addNewSeedButton(module, height);
        }

        protected void addSeedBox(Module module, int x, int y, int width, int height) {
            seedEditBox = new LongEditBoxWidget(x, y, width, height, Component.translatable("selectWorld.trulyrandom.seed.title"), modules.getSeedMemento(module), minecraft.font);
            seedEditBox.setLong(modules.getSeedMemento(module));
            seedEditBox.setValueListener(value -> modules.setSeedMemento(module, seedEditBox.getLong()));
            seedEditBox.setTooltip(Tooltip.create(getSeedTooltipText()));
            seedEditBox.active = module.isImplemented();
        }

        protected void addNewSeedButton(Module module, int height) {
            newSeedButton = Button.builder(Component.translatable("selectWorld.trulyrandom.new_seed"), _ -> seedEditBox.setLong(new Random().nextLong()))
                    .bounds(0, 0, 44, height)
                    .build();
            newSeedButton.setTooltip(Tooltip.create(getNewSeedTooltipText()));
            newSeedButton.active = module.isImplemented();
        }

        private Component getNewSeedTooltipText() {
            if(module.isImplemented()) {
                return Component.translatable("selectWorld.trulyrandom.new_seed.tooltip");
            } else {
                return Component.translatable("selectWorld.trulyrandom.not_implemented")
                        .withStyle(ChatFormatting.DARK_GRAY)
                        .withStyle(ChatFormatting.ITALIC);
            }
        }

        private Component getSeedTooltipText() {
            if (module.isImplemented() && module.isMutable()) {
                return Component.translatable("selectWorld.trulyrandom.seed_tooltip");
            } else if (!module.isMutable()) {
                return Component.translatable("selectWorld.trulyrandom.seed_tooltip_immutable");
            } else {
                return Component.translatable("selectWorld.trulyrandom.not_implemented")
                        .withStyle(ChatFormatting.DARK_GRAY)
                        .withStyle(ChatFormatting.ITALIC);
            }
        }

        @Override
        protected void addElements(RowHelper rowHelper) {
            rowHelper.addChild(seedEditBox);
            rowHelper.addChild(newSeedButton);
        }
    }

    public static class Builder<T extends ModuleElement> {
        private final ModuleWidgetSettings settings;
        private final Module module;
        private final ModuleElementFactory<T> factory;
        private int x;
        private int y;
        private int width;
        private int height;

        private Builder(ModuleWidgetSettings settings, Module module, ModuleElementFactory<T> factory) {
            this.settings = settings;
            this.module = module;
            this.factory = factory;
        }

        public Builder<T> dimensions(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public T build() {
            T moduleElement = factory.create(settings, module, x, y, width, height, 2);
            moduleElement.init();
            moduleElement.columnSpacing(2);
            return moduleElement;
        }
    }

    public interface ModuleElementFactory<T extends ModuleElement> {
        T create(ModuleWidgetSettings settings, Module module, int x, int y, int width, int height, int columns);
    }
}
