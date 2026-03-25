package com.bawnorton.trulyrandom.client.screen;

import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class TargetedTrulyRandomSettingsScreen extends AbstractTrulyRandomSettingsScreen {
    private final PlayerEntity target;
    private final Modules modules;

    public TargetedTrulyRandomSettingsScreen(Screen parent, PlayerEntity target, Modules modules, Consumer<Modules> applier) {
        super(parent, applier);
        this.target = target;
        this.modules = modules;
        this.modules.hideServerSide();
    }

    @Override
    protected Text getContentText() {
        return Text.translatable("selectWorld.trulyrandom.targeted", target.getDisplayName());
    }

    @Override
    public Modules getModules() {
        return modules;
    }

    @Override
    public void close() {
        super.close();
        modules.showServerSide();
    }
}
