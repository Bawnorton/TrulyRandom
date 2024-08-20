package com.bawnorton.trulyrandom.client.screen.module;

import com.bawnorton.trulyrandom.client.screen.module.adapter.ModuleWidgetAdapter;
import com.bawnorton.trulyrandom.client.screen.widget.LongEditBoxWidget;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.util.ModuleAdpatable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.Random;

public class ModuleWidgetSettings extends ModuleAdpatable<ModuleWidgetAdapter> {
    private final MinecraftClient client;
    private final Modules modules;

    public ModuleWidgetSettings(MinecraftClient client, Modules modules) {
        this.client = client;
        this.modules = modules;
    }

    public Widget getWidget(Module module) {
        return getAdapter(module).create(this, module);
    }

    public <T extends ModuleElement> Builder<T> createBuilder(Module module, ModuleElementFactory<T> factory) {
        return new Builder<>(this, module, factory);
    }

    public abstract static class ModuleElement extends GridWidget {
        protected final MinecraftClient client;
        protected final Modules modules;
        protected final Module module;
        private final int columns;

        private ModuleElement(ModuleWidgetSettings settings, Module module, int columns) {
            this.client = settings.client;
            this.modules = settings.modules;
            this.module = module;
            this.columns = columns;
        }

        public void init() {
            addElements(createAdder(columns));
        }

        protected abstract void addElements(GridWidget.Adder adder);
    }

    public static class Title extends ModuleElement {
        private final TextWidget title;
        private final CyclingButtonWidget<Boolean> toggleButton;

        public Title(ModuleWidgetSettings settings, Module module, int x, int y, int width, int height) {
            super(settings, module, 2);
            title = new TextWidget(x, y, width, height, Text.translatable("selectWorld.trulyrandom.%s".formatted(module.name().toLowerCase())), client.textRenderer);
            title.setTooltip(Tooltip.of(getTooltipText()));
            title.alignLeft();
            toggleButton = CyclingButtonWidget.onOffBuilder()
                    .initially(modules.getEnabledMemento(module))
                    .omitKeyText()
                    .build(x, y, 44, height, Text.empty(), (button, value) -> modules.setEnabledMemento(module, value));
            toggleButton.active = module.isImplemented() && (client.world == null || module.isMutable());
        }

        private Text getTooltipText() {
            if (module.isImplemented()) {
                return Text.translatable("selectWorld.trulyrandom.%s.tooltip".formatted(module.name().toLowerCase()));
            } else {
                return Text.translatable("selectWorld.trulyrandom.not_implemented")
                        .formatted(Formatting.DARK_GRAY)
                        .formatted(Formatting.ITALIC);
            }
        }

        @Override
        protected void addElements(Adder adder) {
            adder.add(title);
            adder.add(toggleButton);
        }
    }

    public static class SeedBox extends ModuleElement {
        private final LongEditBoxWidget seedEditBox;
        private final ButtonWidget newSeedButton;

        public SeedBox(ModuleWidgetSettings settings, Module module, int x, int y, int width, int height) {
            super(settings, module, 2);
            seedEditBox = new LongEditBoxWidget(x, y, width, height, Text.translatable("selectWorld.trulyrandom.seed.title"), modules.getSeedMemento(module), client.textRenderer);
            seedEditBox.setLong(modules.getSeedMemento(module));
            seedEditBox.setChangeListener(value -> modules.setSeedMemento(module, seedEditBox.getLong()));
            seedEditBox.setTooltip(Tooltip.of(getSeedTooltipText()));
            newSeedButton = ButtonWidget.builder(Text.translatable("selectWorld.trulyrandom.new_seed"), button -> seedEditBox.setLong(new Random().nextLong()))
                    .dimensions(0, 0, 44, height)
                    .build();
            newSeedButton.setTooltip(Tooltip.of(getNewSeedTooltipText()));
            seedEditBox.active = module.isImplemented();
            newSeedButton.active = module.isImplemented();
        }

        private Text getNewSeedTooltipText() {
            if(module.isImplemented()) {
                return Text.translatable("selectWorld.trulyrandom.new_seed.tooltip");
            } else {
                return Text.translatable("selectWorld.trulyrandom.not_implemented")
                        .formatted(Formatting.DARK_GRAY)
                        .formatted(Formatting.ITALIC);
            }
        }

        private Text getSeedTooltipText() {
            if (module.isImplemented() && module.isMutable()) {
                return Text.translatable("selectWorld.trulyrandom.seed_tooltip");
            } else if (!module.isMutable()) {
                return Text.translatable("selectWorld.trulyrandom.seed_tooltip_immutable");
            } else {
                return Text.translatable("selectWorld.trulyrandom.not_implemented")
                        .formatted(Formatting.DARK_GRAY)
                        .formatted(Formatting.ITALIC);
            }
        }

        @Override
        protected void addElements(Adder adder) {
            adder.add(seedEditBox);
            adder.add(newSeedButton);
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
            T moduleElement = factory.create(settings, module, x, y, width, height);
            moduleElement.init();
            moduleElement.setColumnSpacing(2);
            return moduleElement;
        }
    }

    public interface ModuleElementFactory<T extends ModuleElement> {
        T create(ModuleWidgetSettings settings, Module module, int x, int y, int width, int height);
    }
}
