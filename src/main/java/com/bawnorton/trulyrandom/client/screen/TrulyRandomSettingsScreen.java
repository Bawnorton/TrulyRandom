package com.bawnorton.trulyrandom.client.screen;

import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Consumer;

public class TrulyRandomSettingsScreen extends AbstractTrulyRandomSettingsScreen {
    private final Modules modules;

    public TrulyRandomSettingsScreen(Screen parent, Modules modules, Consumer<Modules> applier) {
        super(parent, applier);
        this.modules = modules;
    }

    @Override
    public Modules getModules() {
        return modules;
    }
}
