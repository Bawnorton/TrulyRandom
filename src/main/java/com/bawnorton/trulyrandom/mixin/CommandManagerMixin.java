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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;execute(Lcom/mojang/brigadier/ParseResults;)I", remap = false, shift = At.Shift.AFTER))
    private void postExecute(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfoReturnable<Integer> cir, @Local ServerCommandSource serverCommandSource) throws CommandSyntaxException {
        PostExecuteCallback.EVENT.invoker().postExecute(serverCommandSource, command);
    }
}
