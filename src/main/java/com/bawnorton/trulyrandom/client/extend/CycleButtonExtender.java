package com.bawnorton.trulyrandom.client.extend;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.CommonComponents;

public interface CycleButtonExtender {
    static CycleButton.Builder<Boolean> colouredOnOffButton(boolean initial) {
        return CycleButton.booleanBuilder(
                CommonComponents.OPTION_ON.copy().withStyle(ChatFormatting.GREEN),
                CommonComponents.OPTION_OFF.copy().withStyle(ChatFormatting.RED),
                initial);
    }
}
