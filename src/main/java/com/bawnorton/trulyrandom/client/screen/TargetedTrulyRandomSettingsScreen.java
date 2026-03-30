package com.bawnorton.trulyrandom.client.screen;

import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public class TargetedTrulyRandomSettingsScreen extends AbstractTrulyRandomSettingsScreen {
    private final Player target;
    private final Modules modules;

    public TargetedTrulyRandomSettingsScreen(Screen parent, Player target, Modules modules, Consumer<Modules> applier) {
        super(parent, applier);
        this.target = target;
        this.modules = modules;
        this.modules.hideServerSide();
    }

    @Override
    protected Component getContentText() {
        return Component.translatable("selectWorld.trulyrandom.targeted", target.getDisplayName());
    }

    @Override
    public Modules getModules() {
        return modules;
    }

    @Override
    public void onClose() {
        super.onClose();
        modules.showServerSide();
    }
}
