package com.bawnorton.trulyrandom.command;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentType;
import com.bawnorton.trulyrandom.event.PostExecuteCallback;
import com.bawnorton.trulyrandom.network.packet.s2c.OpenRandomiserScreenS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.RequestRandomiserS2CPacket;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class TrulyRandomSettingsCommand {
    private final PostExecuteRunner runner;

    public TrulyRandomSettingsCommand() {
        runner = new PostExecuteRunner();
        runner.register();
    }

    private static void executeOpenRandomiserScreen(CommandContext<ServerCommandSource> context, Selection selection) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity executor = source.getPlayer();
        if (executor == null) {
            source.sendFeedback(() -> Text.literal("You must be a player to use this command"), true);
            return;
        }

        ServerPlayerEntity target = null;
        if (selection == Selection.PLAYER) {
            target = EntityArgumentType.getPlayer(context, "player");
        } else if (selection == Selection.SELF) {
            target = executor;
        }

        if (target == null) {
            ServerRandomiser randomiser = TrulyRandom.getRandomiser(source.getServer());
            ServerPlayNetworking.send(executor, new OpenRandomiserScreenS2CPacket(randomiser.getModules()));
        } else {
            ServerPlayNetworking.send(target, new RequestRandomiserS2CPacket(executor.getUuid()));
        }
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("trulyrandom")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("selection", SetStringArgumentType.of("server", "all"))
                        .executes(context -> execute(context, Selection.SERVER)))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(context -> execute(context, Selection.PLAYER))
                        .then(CommandManager.literal("test")
                                .then(CommandManager.literal("drops")
                                        .executes(context -> {
                                            context.getSource()
                                                    .sendFeedback(() -> Text.of("Triggering all loot tables, world will lag for a bit"), true);
                                            ServerPlayerEntity player = context.getSource()
                                                    .getPlayer();
                                            assert player != null;
                                            ServerWorld world = player.getServerWorld();
                                            BlockPos up = player.getBlockPos()
                                                    .add(0, 20, 0);
                                            Registries.BLOCK.forEach((block -> {
                                                world.setBlockState(up, block.getDefaultState(), 0);
                                                world.breakBlock(up, true, player);
                                            }));
                                            Registries.ENTITY_TYPE.forEach((entityType -> {
                                                Entity entity = entityType.create(world);
                                                if (!(entity instanceof LivingEntity))
                                                    return;
                                                entity.updatePosition(player.getX(), player.getY() + 1, player.getZ());
                                                world.spawnEntity(entity);
                                                entity.damage(world.getDamageSources()
                                                        .playerAttack(player), Float.MAX_VALUE);
                                            }));
                                            return 1;
                                        })
                                )
                                .then(CommandManager.literal("newseed")
                                        .executes(context -> {
                                            context.getSource()
                                                    .sendFeedback(() -> Text.of("Unimplemented"), true);
                                            return 1;
                                        })
                                )
                        )
                        .executes(context -> execute(context, Selection.SELF)))
        );
    }

    private int execute(CommandContext<ServerCommandSource> context, Selection selection) {
        runner.setRunnable((() -> executeOpenRandomiserScreen(context, selection)));
        return 1;
    }

    private enum Selection {
        PLAYER,
        SELF,
        SERVER
    }


    private static class PostExecuteRunner implements PostExecuteCallback {
        private CommandRunnable runnable = () -> {};
        private boolean run = false;

        @Override
        public void postExecute(ServerCommandSource commandSource, String command) throws CommandSyntaxException {
            if (run) {
                runnable.run();
                run = false;
            }
        }

        public void register() {
            PostExecuteCallback.EVENT.register(this);
        }

        public void setRunnable(CommandRunnable runnable) {
            this.runnable = runnable;

        }

        @FunctionalInterface
        public interface CommandRunnable {
            void run() throws CommandSyntaxException;
        }
    }
}
