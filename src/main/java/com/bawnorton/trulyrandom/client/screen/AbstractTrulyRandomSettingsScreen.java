package com.bawnorton.trulyrandom.client.screen;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.screen.module.adapter.DefaultModuleWidgetAdapter;
import com.bawnorton.trulyrandom.client.screen.module.adapter.SettingsWidgetAdapter;
import com.bawnorton.trulyrandom.client.screen.module.settings.*;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.ModuleCategory;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.state.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractTrulyRandomSettingsScreen extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 23, 36);
    private final Screen parent;
    private final Consumer<Modules> applier;
    protected ModuleList moduleList;

    protected AbstractTrulyRandomSettingsScreen(Screen parent, Consumer<Modules> applier) {
        super(Component.translatable("selectWorld.trulyrandom"));
        this.parent = parent;
        this.applier = applier;
    }

    @Override
    protected void init() {
        addHeader();
        addModules();
        addFooter();
        layout.visitWidgets(this::addRenderableWidget);
        repositionElements();
    }

    protected void addHeader() {
        layout.addToHeader(new MultiLineTextWidget(getContentText(), font), positioner -> positioner.paddingTop(5));
    }

    protected void addModules() {
        List<Module> modules = getModules().asList();
        modules.removeIf(module -> !getModules().isVisible(module));
        modules.sort(Comparator.comparingInt(Module::ordinal));
        ModuleWidgetSettings moduleSettings = new ModuleWidgetSettings(minecraft, getModules());
        moduleSettings.registerAdapter(Module.RECIPES, new SettingsWidgetAdapter<>(RecipeModuleState.class, RecipeModuleSettings::new));
        moduleSettings.registerAdapter(Module.BLOCK_MODELS, new SettingsWidgetAdapter<>(BlockModelModuleState.class, BlockModelModuleSettings::new));
        moduleSettings.registerAdapter(Module.ITEM_MODELS, new SettingsWidgetAdapter<>(ItemModelModuleState.class, ItemModelModuleSettings::new));
        moduleSettings.registerAdapter(Module.LOOT_TABLES, new SettingsWidgetAdapter<>(LootModuleState.class, LootModuleSettings::new));
        moduleSettings.registerAdapter(Module.STRUCTURES, new SettingsWidgetAdapter<>(StructureModuleState.class, StructureModuleSettings::new));
        moduleSettings.registerAdapter(Module.BLOCK_PALETTE, new SettingsWidgetAdapter<>(BlockPaletteModuleState.class, BlockPaletteModuleSettings::new));
        moduleSettings.setDefaultAdapter(new DefaultModuleWidgetAdapter());
        moduleList = layout.addToContents(new ModuleList(getModules(), moduleSettings));
        moduleList.populateChildren();
        moduleList.setScrollAmount(0);
    }

    protected void addFooter() {
        GridLayout.RowHelper rowHelper = layout.addToFooter(new GridLayout().columnSpacing(10)).createRowHelper(2);
        rowHelper.addChild(Button.builder(CommonComponents.GUI_DONE, _ -> applyAndClose()).build());
        rowHelper.addChild(Button.builder(CommonComponents.GUI_CANCEL, _ -> forgetAndClose()).build());
    }

    protected Component getContentText() {
        return Component.translatable("selectWorld.trulyrandom.info");
    }

    protected abstract Modules getModules();

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    private void applyAndClose() {
        getModules().confirm();
        applier.accept(getModules());
        onClose();
    }

    private void forgetAndClose() {
        getModules().cancel();
        onClose();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
        moduleList.updateSize(width, layout);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.text(
                minecraft.font,
                Component.literal(TrulyRandom.VERSION.getFriendlyString()),
                5,
                5,
                CommonColors.LIGHT_GRAY,
                false
        );
    }

    private abstract static class ModuleEntry extends ContainerObjectSelectionList.Entry<ModuleEntry> {
    }

    private class CategoryLabelEntry extends ModuleEntry {
        private final ModuleCategory category;

        public CategoryLabelEntry(ModuleCategory category) {
            this.category = category;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            graphics.centeredText(
                    AbstractTrulyRandomSettingsScreen.this.minecraft.font,
                    category.getDisplayName().withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD),
                    getContentXMiddle(),
                    getContentY() + 5,
                    -1
            );
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of();
        }
    }

    private static class LayoutDerivedEntry extends ModuleEntry {
        private final List<AbstractWidget> children;
        private final GridLayout layout;

        private LayoutDerivedEntry(GridLayout layout) {
            this.children = new ArrayList<>();
            this.layout = layout;
            this.layout.visitWidgets(children::add);
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            layout.setX(getContentX());
            layout.setY(getContentY());
            layout.arrangeElements();
            layout.visitWidgets(widget -> widget.extractRenderState(graphics, mouseX, mouseY, a));
        }


        @Override
        public List<? extends NarratableEntry> narratables() {
            return children;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return children;
        }
    }

    protected class ModuleList extends ContainerObjectSelectionList<ModuleEntry> {
        private final Modules modules;
        private final ModuleWidgetSettings settings;

        public ModuleList(Modules modules, ModuleWidgetSettings settings) {
            super(
                    Minecraft.getInstance(),
                    AbstractTrulyRandomSettingsScreen.this.width,
                    AbstractTrulyRandomSettingsScreen.this.layout.getContentHeight(),
                    AbstractTrulyRandomSettingsScreen.this.layout.getHeaderHeight(),
                    24
            );
            this.modules = modules;
            this.settings = settings;
            this.populateChildren();
        }

        private void populateChildren() {
            clearEntries();
            Map<ModuleCategory, Map<Module, LayoutDerivedEntry>> entries = new HashMap<>();
            for (Module module : modules) {
                GridLayout widget = settings.getWidget(module);
                entries.computeIfAbsent(module.getCategory(), _ -> new HashMap<>())
                        .put(module, new LayoutDerivedEntry(
                                widget
                        ));
            }
            entries.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(ModuleCategory::ordinal)))
                    .forEach(
                            e -> {
                                this.addEntry(
                                        new CategoryLabelEntry(e.getKey())
                                );
                                e.getValue()
                                    .entrySet()
                                    .stream()
                                    .sorted(Map.Entry.comparingByKey(Comparator.comparing(Module::ordinal)))
                                    .forEach(v -> this.addEntry(v.getValue(), 40));
                            }
                    );
        }
    }
}