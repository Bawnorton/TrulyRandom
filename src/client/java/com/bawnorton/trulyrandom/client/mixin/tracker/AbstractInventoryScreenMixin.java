package com.bawnorton.trulyrandom.client.mixin.tracker;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin extends HandledScreenMixin {
    protected AbstractInventoryScreenMixin(Text title) {
        super(title);
    }

    @ModifyVariable(method = "drawStatusEffects", at = @At("STORE"), ordinal = 2)
    protected int changeEffectX(int x) {
        return x;
    }
}
