package com.bawnorton.trulyrandom.client.screen;

import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Consumer;

public class TrulyRandomSettingsScreen extends AbstractTrulyRandomSettingsScreen {
    private final Modules modules;

    public TrulyRandomSettingsScreen(Screen parent, Modules modules, Consumer<Modules> applier) {
        super(parent, applier);
        this.modules = modules;
    }

    public TrulyRandomSettingsScreen(Screen parent, Consumer<Modules> applier) {
        this(parent, new Modules(), applier);
    }

    @Override
    public Modules getModules() {
        return modules;
    }
}
