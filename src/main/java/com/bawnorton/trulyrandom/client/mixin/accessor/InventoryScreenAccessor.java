package com.bawnorton.trulyrandom.client.mixin.accessor;

import net.minecraft.client.gui.screens.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(InventoryScreen.class)
public interface InventoryScreenAccessor {
    @Accessor
    void setMouseDown(boolean mouseDown);
}
