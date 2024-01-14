package com.bawnorton.trulyrandom.client.screen.widget;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.*;

public class ColumnedOptionGrid {
    private final List<Option> options;

    private ColumnedOptionGrid(List<Option> options) {
        this.options = options;
    }

    public static ColumnedOptionGrid.Builder builder(int columns, int width) {
        return new ColumnedOptionGrid.Builder(columns, width);
    }

    public void refresh() {
        options.forEach(Option::refresh);
    }

    public static class Builder {
        private final int columns;
        private final int width;
        private final List<ColumnedOptionGrid.OptionBuilder> options = new ArrayList<>();
        int marginLeft;
        int rowSpacing = 5;
        int columnSpacing = 5;
        int rows;
        int columnIndex;

        private Builder(int columns, int width) {
            this.columns = columns;
            this.width = width;
        }

        void incrementRowIndex() {
            rows += 2;
        }

        void incrementColumnIndex() {
            columnIndex += 2;
            if (columnIndex >= columns * 2) {
                columnIndex = 0;
                incrementRowIndex();
            }
        }

        public OptionBuilder add(Text text) {
            OptionBuilder optionBuilder = new OptionBuilder(text, width / columns - 44);
            options.add(optionBuilder);
            return optionBuilder;
        }

        public void build(Consumer<Widget> widgetConsumer) {
            GridWidget widget = new GridWidget().setColumnSpacing(columnSpacing).setRowSpacing(rowSpacing);
            for (int column = 0; column < this.columns; column += 2) {
                widget.add(EmptyWidget.ofWidth(this.width / this.columns - 44), 0, column);
                widget.add(EmptyWidget.ofWidth(44), 0, column + 1);
            }
            List<Option> options = new ArrayList<>();
            this.rows = 0;
            this.columnIndex = 0;
            for (OptionBuilder optionBuilder : this.options) {
                options.add(optionBuilder.build(this, widget, rows, columnIndex));
                incrementColumnIndex();
            }

            widget.refreshPositions();
            widgetConsumer.accept(widget);
            ColumnedOptionGrid columnedOptionGrid = new ColumnedOptionGrid(options);
            columnedOptionGrid.refresh();
        }
    }

    public record Option(CyclingButtonWidget<Boolean> button, LongEditBoxWidget seedBox, BooleanSupplier getter, LongSupplier seedGetter, BooleanSupplier toggleable) {
        public void refresh() {
            button.setValue(getter.getAsBoolean());
            seedBox.setLong(seedGetter.getAsLong());
            if (toggleable != null) {
                button.active = toggleable.getAsBoolean();
                seedBox.active = toggleable.getAsBoolean();
            }
        }
    }

    public static class OptionBuilder {
        private final Text text;
        private final int width;
        private BooleanSupplier getter;
        private BooleanConsumer setter;
        private LongSupplier seedGetter;
        private LongConsumer seedSetter;
        private BooleanSupplier toggleable;
        private Supplier<Text> tooltip;
        private Supplier<Text> seedTooltip;

        private OptionBuilder(Text text, int width) {
            this.text = text;
            this.width = width;
        }

        public OptionBuilder value(BooleanSupplier getter, BooleanConsumer setter) {
            this.getter = getter;
            this.setter = setter;
            return this;
        }

        public OptionBuilder seed(LongSupplier seedGetter, LongConsumer seedSetter) {
            this.seedGetter = seedGetter;
            this.seedSetter = seedSetter;
            return this;
        }

        public OptionBuilder toggleable(BooleanSupplier toggleable) {
            this.toggleable = toggleable;
            return this;
        }

        public OptionBuilder tooltip(Supplier<Text> tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public OptionBuilder seedTooltip(Supplier<Text> seedTooltip) {
            this.seedTooltip = seedTooltip;
            return this;
        }

        private int columnAdjustedWidth(Builder builder) {
            return (builder.width - builder.marginLeft - width) / builder.columns - 10;
        }

        Option build(Builder builder, GridWidget widget, int row, int column) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            TextWidget textWidget = new TextWidget(text, textRenderer).alignLeft();
            textWidget.setWidth(columnAdjustedWidth(builder));
            widget.add(textWidget, row, column, widget.copyPositioner()
                    .relative(0, 0.5f)
                    .marginLeft(builder.marginLeft));
            CyclingButtonWidget.Builder<Boolean> cyclingBuilder = CyclingButtonWidget.onOffBuilder(this.getter.getAsBoolean());
            cyclingBuilder.omitKeyText();
            if (tooltip != null) {
                Tooltip tooltip = Tooltip.of(this.tooltip.get());
                cyclingBuilder.tooltip(value -> tooltip);
                cyclingBuilder.narration(button -> ScreenTexts.joinSentences(text, button.getGenericNarrationMessage(), this.tooltip.get()));
            } else {
                cyclingBuilder.narration(button -> ScreenTexts.joinSentences(text, button.getGenericNarrationMessage()));
            }

            CyclingButtonWidget<Boolean> button = cyclingBuilder.build(0, 0, 44, 20, Text.empty(), (b, value) -> setter.accept((boolean) value));
            widget.add(button, row, column + 1, widget.copyPositioner().alignRight());

            LongEditBoxWidget seedBox = new LongEditBoxWidget(textRenderer, 0, 0, columnAdjustedWidth(builder) + 44, 18, Text.of("Seed"), seedGetter.getAsLong());
            seedBox.setChangeListener(value -> seedSetter.accept(seedBox.getLong()));
            if (seedTooltip != null) {
                Tooltip tooltip = Tooltip.of(seedTooltip.get());
                seedBox.setTooltip(tooltip);
            }
            widget.add(seedBox, row + 1, column, widget.copyPositioner().marginLeft(builder.marginLeft));

            ButtonWidget newSeedButton = ButtonWidget.builder(Text.of("NEW"), b -> seedBox.setLong(new Random().nextLong())).width(44).build();
            widget.add(newSeedButton, row + 1, column + 1, widget.copyPositioner().alignRight());

            if (toggleable != null) {
                button.active = toggleable.getAsBoolean();
                seedBox.active = toggleable.getAsBoolean();
                newSeedButton.active = toggleable.getAsBoolean();
            }

            return new Option(button, seedBox, getter, seedGetter, toggleable);
        }
    }
}
