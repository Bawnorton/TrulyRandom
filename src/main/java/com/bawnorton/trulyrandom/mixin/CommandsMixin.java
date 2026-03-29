package com.bawnorton.trulyrandom.mixin;

import com.bawnorton.trulyrandom.event.PostExecuteCallback;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.execution.ExecutionContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(Commands.class)
abstract class CommandsMixin {
    @WrapOperation(
            method = "performCommand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/commands/Commands;executeCommandInContext(Lnet/minecraft/commands/CommandSourceStack;Ljava/util/function/Consumer;)V"
            )
    )
    private void postExecute(CommandSourceStack executionContext, Consumer<ExecutionContext<CommandSourceStack>> gameRules, Operation<Void> original) throws CommandSyntaxException {
        original.call(executionContext, gameRules);
        PostExecuteCallback.EVENT.invoker().postExecute(executionContext);
    }
}
