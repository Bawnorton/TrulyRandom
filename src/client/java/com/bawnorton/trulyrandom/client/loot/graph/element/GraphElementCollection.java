package com.bawnorton.trulyrandom.client.loot.graph.element;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphElementCollection extends GraphElement implements Iterable<GraphElement> {
    private final List<GraphElement> elements;

    public GraphElementCollection(List<GraphElement> elements) {
        this.elements = elements;
        elements.forEach(element -> {
            element.getTo().forEach(this::addTo);
            element.getFrom().forEach(this::addFrom);
            element.getTo().clear();
            element.getFrom().clear();
        });
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int mouseX, int mouseY, int x, int y, float scale) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        float collectionScale = 1.4f;
        matrices.scale(1f / collectionScale, 1f / collectionScale, 1);
        x = (int) (x * collectionScale);
        y = (int) (y * collectionScale);
        if(elements.size() >= 3) {
            GraphElement first = elements.getFirst();
            first.render(context, client, mouseX, mouseY, x, y - 8, scale / collectionScale);
            GraphElement second = elements.get(1);
            second.render(context, client, mouseX, mouseY, x - 8, y + 8, scale / collectionScale);
            GraphElement third = elements.get(2);
            third.render(context, client, mouseX, mouseY, x + 8, y + 8, scale / collectionScale);
        } else if (elements.size() == 2) {
            GraphElement first = elements.getFirst();
            first.render(context, client, mouseX, mouseY, x - 8, y, scale / collectionScale);
            GraphElement second = elements.get(1);
            second.render(context, client, mouseX, mouseY, x + 8, y, scale / collectionScale);
        }
        matrices.pop();
    }

    @Override
    protected Text getTooltip() {
        return Text.empty();
    }

    @Override
    public void drawTooltip(DrawContext context, int mouseX, int mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        List<Text> tooltip = new ArrayList<>();
        for (GraphElement element : elements) {
            tooltip.add(element.getTooltip());
        }
        context.drawTooltip(
                textRenderer,
                tooltip,
                mouseX - tooltip.stream().mapToInt(textRenderer::getWidth).max().orElse(0) - 15,
                mouseY - (textRenderer.fontHeight) * tooltip.size()
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
