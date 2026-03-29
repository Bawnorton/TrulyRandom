package com.bawnorton.trulyrandom.client.mixin.accessor;

import net.minecraft.client.gui.screens.recipebook.RecipeBookWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookWidget.class)
public interface RecipeBookWidgetAccessor {
    @Accessor("TEXTURE")
    static Identifier getTexture() {
        throw new AssertionError();
    }
}
