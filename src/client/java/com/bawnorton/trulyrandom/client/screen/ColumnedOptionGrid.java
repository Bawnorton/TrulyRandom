package com.bawnorton.trulyrandom.client.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class ColumnedOptionGrid {
    private final List<Option> options;

    private ColumnedOptionGrid(List<Option> options) {
        this.options = options;
    }

    public void refresh() {
        options.forEach(Option::refresh);
    }

    public static ColumnedOptionGrid.Builder builder(int columns, int width) {
        return new ColumnedOptionGrid.Builder(columns, width);
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
            rows++;
        }

        void incrementColumnIndex() {
            columnIndex += 2;
            if (columnIndex >= columns * 2) {
                columnIndex = 0;
                incrementRowIndex();
            }
        }

        public OptionBuilder add(Text text, BooleanSupplier getter, Consumer<Boolean> setter) {
            OptionBuilder optionBuilder = new OptionBuilder(text, getter, setter, 44);
            options.add(optionBuilder);
            return optionBuilder;
        }

        public Builder marginLeft(int marginLeft) {
            this.marginLeft = marginLeft;
            return this;
        }

        public Builder rowSpacing(int rowSpacing) {
            this.rowSpacing = rowSpacing;
            return this;
        }

        public Builder columnSpacing(int columnSpacing) {
            this.columnSpacing = columnSpacing;
            return this;
        }

        public void build(Consumer<Widget> widgetConsumer) {
            GridWidget widget = new GridWidget().setColumnSpacing(columnSpacing).setRowSpacing(rowSpacing);
            for(int column = 0; column < this.columns; column += 2) {
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

    public record Option(CyclingButtonWidget<Boolean> button, BooleanSupplier getter, BooleanSupplier toggleable) {
        public void refresh() {
            button.setValue(getter.getAsBoolean());
            if(toggleable != null) {
                button.active = toggleable.getAsBoolean();
            }
        }
    }

    public static class OptionBuilder {
        private final Text text;
        private final BooleanSupplier getter;
        private final Consumer<Boolean> setter;
        private BooleanSupplier toggleable;
        private Text tooltip;
        private final int width;

        private OptionBuilder(Text text, BooleanSupplier getter, Consumer<Boolean> setter, int width) {
            this.text = text;
            this.getter = getter;
            this.setter = setter;
            this.width = width;
        }

        public OptionBuilder toggleable(BooleanSupplier toggleable) {
            this.toggleable = toggleable;
            return this;
        }

        public OptionBuilder tooltip(Text tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        private int columnAdjustedWidth(Builder builder) {
            return (builder.width - builder.marginLeft - width) / builder.columns - 10;
        }

        Option build(Builder builder, GridWidget widget, int row, int column) {
            TextWidget textWidget = new TextWidget(text, MinecraftClient.getInstance().textRenderer).alignLeft();
            textWidget.setWidth(columnAdjustedWidth(builder));
            widget.add(textWidget, row, column, widget.copyPositioner().relative(0, 0.5f).marginLeft(builder.marginLeft));
            CyclingButtonWidget.Builder<Boolean> cyclingBuilder = CyclingButtonWidget.onOffBuilder(this.getter.getAsBoolean());
            cyclingBuilder.omitKeyText();
            if(tooltip != null) {
                Tooltip tooltip = Tooltip.of(this.tooltip);
                cyclingBuilder.tooltip(value -> tooltip);
            }
            if(tooltip != null) {
                cyclingBuilder.narration(button -> ScreenTexts.joinSentences(text, button.getGenericNarrationMessage(), this.tooltip));
            } else {
                cyclingBuilder.narration(button -> ScreenTexts.joinSentences(text, button.getGenericNarrationMessage()));
            }

            CyclingButtonWidget<Boolean> button = cyclingBuilder.build(0, 0, width, 20, Text.empty(), (b, value) -> setter.accept(value));
            if (toggleable != null) {
                button.active = toggleable.getAsBoolean();
            }

            widget.add(button, row, column + 1, widget.copyPositioner().alignRight());
            return new Option(button, getter, toggleable);
        }
    }
}
