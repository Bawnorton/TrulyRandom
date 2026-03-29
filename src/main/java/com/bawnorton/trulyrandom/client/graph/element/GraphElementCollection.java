package com.bawnorton.trulyrandom.client.graph.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphElementCollection extends GraphElement implements Iterable<GraphElement> {
    private final List<GraphElement> elements;

    public GraphElementCollection(List<GraphElement> elements) {
        this.elements = elements;
        elements.forEach(element -> {
            element.getTo().forEach(to -> addTo(to, element.getConnection(to)));
            element.getFrom().forEach(from -> addFrom(from, element.getConnection(from)));
            element.getTo().clear();
            element.getFrom().clear();
        });
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        Matrix3x2fStack matrices = graphics.pose();
        matrices.pushMatrix();
        float collectionScale = 1.4f;
        matrices.scale(1f / collectionScale, 1f / collectionScale);
        x = (int) (x * collectionScale);
        y = (int) (y * collectionScale);
        if(elements.size() >= 3) {
            GraphElement first = elements.getFirst();
            first.extractRenderState(graphics, minecraft, mouseX, mouseY, x, y - 8, scale / collectionScale);
            GraphElement second = elements.get(1);
            second.extractRenderState(graphics, minecraft, mouseX, mouseY, x - 8, y + 8, scale / collectionScale);
            GraphElement third = elements.get(2);
            third.extractRenderState(graphics, minecraft, mouseX, mouseY, x + 8, y + 8, scale / collectionScale);
        } else if (elements.size() == 2) {
            GraphElement first = elements.getFirst();
            first.extractRenderState(graphics, minecraft, mouseX, mouseY, x - 8, y, scale / collectionScale);
            GraphElement second = elements.get(1);
            second.extractRenderState(graphics, minecraft, mouseX, mouseY, x + 8, y, scale / collectionScale);
        }
        matrices.popMatrix();
    }

    @Override
    protected Component getTooltip() {
        return Component.empty();
    }

    @Override
    public void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        List<Component> tooltip = new ArrayList<>();
        for (GraphElement element : elements) {
            tooltip.add(element.getTooltip());
        }
        graphics.setComponentTooltipForNextFrame(
                font,
                tooltip,
                mouseX - tooltip.stream().mapToInt(font::width).max().orElse(0) - 15,
                mouseY - (font.lineHeight) * tooltip.size()
        );
    }

    @NotNull
    @Override
    public Iterator<GraphElement> iterator() {
        return elements.iterator();
    }

    @Override
    public String toString() {
        return "Collection[%s]".formatted(elements);
    }
}
