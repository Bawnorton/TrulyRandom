package com.bawnorton.trulyrandom.client.screen;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import com.bawnorton.trulyrandom.client.screen.widget.ColumnedOptionGrid;
import com.bawnorton.trulyrandom.random.Modules;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class TrulyRandomSettingsScreen extends Screen {
    private final String randomSeed = String.valueOf(new Random().nextLong());
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    private final Screen parent;
    private final BiConsumer<Modules, Long> applier;
    private final Modules modules = new Modules();
    private EditBoxWidget seedBox;

    public TrulyRandomSettingsScreen(Screen parent, BiConsumer<Modules, Long> applier) {
        super(Text.translatable("selectWorld.trulyrandom"));
        this.parent = parent;
        this.applier = applier;
    }

    @Override
    protected void init() {
        GridWidget.Adder header = layout.addHeader(new GridWidget().setColumnSpacing(10)).createAdder(2);
        header.add(new TextWidget(Text.translatable("selectWorld.trulyrandom.seed.title"), textRenderer)).setHeight(18);
        seedBox = header.add(new EditBoxWidget(textRenderer, 0, 0, 150, 18, Text.translatable("selectWorld.trulyrandom.seed"), Text.of("")));
        seedBox.setTooltip(Tooltip.of(Text.translatable("selectWorld.trulyrandom.seed.tooltip")));
        AtomicReference<String> lastSet = new AtomicReference<>();
        seedBox.setChangeListener(s -> {
            if (s.isEmpty() || s.equals("-") || s.equals(lastSet.get())) {
                lastSet.set(s);
                return;
            }
            try {
                Long.parseLong(s);
            } catch (NumberFormatException e) {
                seedBox.setText(lastSet.get());
                return;
            }
            lastSet.set(s);
        });
        if (client.world != null) {
            seedBox.setText(String.valueOf(TrulyRandomClient.getRandomiser().getLocalSeed()));
        } else {
            seedBox.setText(randomSeed);
        }
        DirectionalLayoutWidget content = layout.addBody(DirectionalLayoutWidget.vertical());
        content.add(
                new MultilineTextWidget(Text.translatable("selectWorld.trulyrandom.info", KeybindManager.OPEN_RANDOMISER_GUI.getKeybind().getBoundKeyLocalizedText()), textRenderer).setMaxWidth(340),
                positioner -> positioner.marginBottom(15)
        );
        ColumnedOptionGrid.Builder builder = ColumnedOptionGrid.builder(2, 310);
        modules.forEach(module -> builder.add(Text.translatable("selectWorld.trulyrandom." + module.name().toLowerCase()),
                () -> modules.getMemento(module),
                enabled -> modules.setMemento(module, enabled))
                .toggleable(module::isImplemented)
                .tooltip(module.isImplemented() ?
                        Text.translatable("selectWorld.trulyrandom." + module.name().toLowerCase() + ".tooltip") :
                        Text.translatable("selectWorld.trulyrandom.not_implemented").formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC)
                )
                .toggleable(() -> {
                    if(client.world == null) return true;
                    return module.isMutable();
                })
        );
        builder.build(content::add);
        GridWidget.Adder adder = layout.addFooter(new GridWidget().setColumnSpacing(10)).createAdder(2);
        adder.add(ButtonWidget.builder(ScreenTexts.DONE, button -> applyAndClose()).build());
        adder.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> forgetAndClose()).build());
        layout.forEachChild(this::addDrawableChild);
        initTabNavigation();
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(parent);
    }

    private void applyAndClose() {
        modules.confirm();
        applier.accept(modules, Long.parseLong(seedBox.getText().isEmpty() ? randomSeed : seedBox.getText()));
        close();
    }

    private void forgetAndClose() {
        modules.cancel();
        close();
    }

    @Override
    protected void initTabNavigation() {
        layout.refreshPositions();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        if (client.world != null) return;

        context.setShaderColor(0.125F, 0.125F, 0.125F, 1.0F);
        context.drawTexture(OPTIONS_BACKGROUND_TEXTURE, 0, layout.getHeaderHeight(), 0.0F, 0.0F, width, height - layout.getHeaderHeight() - layout.getFooterHeight(), 32, 32);
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
