package com.bawnorton.trulyrandom.client.screen.widget;

import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Util;

import java.util.function.Consumer;

// Source: MC 1.20.3+
public class DirectionalLayoutWidget implements LayoutWidget {
    private final GridWidget grid;
    private final DirectionalLayoutWidget.DisplayAxis axis;
    private int currentIndex = 0;

    private DirectionalLayoutWidget(DirectionalLayoutWidget.DisplayAxis var1) {
        this(0, 0, var1);
    }

    public DirectionalLayoutWidget(int var1, int var2, DirectionalLayoutWidget.DisplayAxis var3) {
        this.grid = new GridWidget(var1, var2);
        this.axis = var3;
    }

    public static DirectionalLayoutWidget vertical() {
        return new DirectionalLayoutWidget(DirectionalLayoutWidget.DisplayAxis.VERTICAL);
    }

    public static DirectionalLayoutWidget horizontal() {
        return new DirectionalLayoutWidget(DirectionalLayoutWidget.DisplayAxis.HORIZONTAL);
    }

    public DirectionalLayoutWidget spacing(int spacing) {
        this.axis.setSpacing(this.grid, spacing);
        return this;
    }

    public Positioner copyPositioner() {
        return this.grid.copyPositioner();
    }

    public Positioner getMainPositioner() {
        return this.grid.getMainPositioner();
    }

    public <T extends Widget> T add(T widget, Positioner positioner) {
        return this.axis.add(this.grid, widget, this.currentIndex++, positioner);
    }

    public <T extends Widget> T add(T widget) {
        return this.add(widget, this.copyPositioner());
    }

    public <T extends Widget> T add(T widget, Consumer<Positioner> callback) {
        return this.axis.add(this.grid, widget, this.currentIndex++, Util.make(this.copyPositioner(), callback));
    }

    @Override
    public void forEachElement(Consumer<Widget> consumer) {
        this.grid.forEachElement(consumer);
    }

    @Override
    public void refreshPositions() {
        this.grid.refreshPositions();
    }

    @Override
    public int getWidth() {
        return this.grid.getWidth();
    }

    @Override
    public int getHeight() {
        return this.grid.getHeight();
    }

    @Override
    public int getX() {
        return this.grid.getX();
    }

    @Override
    public void setX(int x) {
        this.grid.setX(x);
    }

    @Override
    public int getY() {
        return this.grid.getY();
    }

    @Override
    public void setY(int y) {
        this.grid.setY(y);
    }

    public enum DisplayAxis {
        HORIZONTAL,
        VERTICAL;

        void setSpacing(GridWidget grid, int spacing) {
            switch (this) {
                case HORIZONTAL:
                    grid.setColumnSpacing(spacing);
                    break;
                case VERTICAL:
                    grid.setRowSpacing(spacing);
            }
        }

        public <T extends Widget> T add(GridWidget grid, T widget, int index, Positioner positioner) {
            return switch (this) {
                case HORIZONTAL -> grid.add(widget, 0, index, positioner);
                case VERTICAL -> grid.add(widget, index, 0, positioner);
            };
        }
    }
}