package com.bawnorton.trulyrandom.mixin;

import com.bawnorton.trulyrandom.event.PostExecuteCallback;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/CommandManager;callWithContext(Lnet/minecraft/server/command/ServerCommandSource;Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private <T> void postExecute(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfo ci, @Local ServerCommandSource commandSource) throws CommandSyntaxException {
        PostExecuteCallback.EVENT.invoker().postExecute(commandSource);
    }
}
