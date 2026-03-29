package com.bawnorton.trulyrandom.command;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.command.argument.SetStringArgumentType;
import com.bawnorton.trulyrandom.event.PostExecuteCallback;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundOpenRandomiserScreenPacket;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundRequestOtherClientRandomiserPacket;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceOrIdArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

public class TrulyRandomCommand {
    private final PostExecuteRunner runner;

    public TrulyRandomCommand() {
        runner = new PostExecuteRunner();
        runner.register();
    }

    private static void executeOpenRandomiserScreen(CommandContext<CommandSourceStack> context, Selection selection) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer executor = source.getPlayer();
        if (executor == null) {
            source.sendFailure(Component.literal("You must be a player to use this command"));
            return;
        }

        ServerPlayer target = null;
        if (selection == Selection.PLAYER) {
            target = EntityArgument.getPlayer(context, "player");
        } else if (selection == Selection.SELF) {
            target = executor;
        }

        if (target == null) {
            ServerRandomiser randomiser = TrulyRandom.getRandomiser(source.getServer());
            ServerPlayNetworking.send(executor, new ClientboundOpenRandomiserScreenPacket(randomiser.getModules()));
        } else {
            ServerPlayNetworking.send(target, new ClientboundRequestOtherClientRandomiserPacket(executor.getUUID()));
        }
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        dispatcher.register(Commands.literal("trulyrandom")
                .requires(Commands.hasPermission(Commands.LEVEL_MODERATORS))
                .then(Commands.literal("settings")
                        .then(Commands.argument("selection", SetStringArgumentType.of("server", "all"))
                                .executes(context -> execute(context, Selection.SERVER))
                        )
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> execute(context, Selection.PLAYER))
                        )
                )
                .then(Commands.literal("drop")
                        .then(Commands.argument("loot_table", ResourceOrIdArgument.lootTable(buildContext))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ServerRandomiser randomiser = TrulyRandom.getRandomiser(source.getServer());
                                    Holder<LootTable> lootTable = ResourceOrIdArgument.LootTableArgument.getLootTable(context, "loot_table");
                                    Optional<ResourceKey<LootTable>> keyOptional = lootTable.unwrapKey();
                                    if (keyOptional.isEmpty()) {
                                        context.getSource().sendFailure(Component.literal("Could not find loot table key for \"%s\"".formatted(lootTable.getRegisteredName())));
                                        return 0;
                                    }
                                    String to = lootTable.getRegisteredName();
                                    String from = randomiser.getLootRandomiser().getSourceTable(keyOptional.orElseThrow()).identifier().toString();
                                    context.getSource().sendSuccess(
                                            () -> Component.literal("%s".formatted(to))
                                                    .append(CommonComponents.NEW_LINE)
                                                    .append("drops from")
                                                    .append(CommonComponents.NEW_LINE)
                                                    .append("%s".formatted(from)),
                                            true
                                    );
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("recipe")
                        .then(Commands.argument("recipe", ItemArgument.item(buildContext))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ServerRandomiser randomiser = TrulyRandom.getRandomiser(source.getServer());
                                    ItemInput itemInput = ItemArgument.getItem(context, "recipe");
                                    Item item = itemInput.item().value();
                                    List<ResourceKey<Recipe<?>>> recipes = randomiser.getRecipeRandomiser().getRecipesForOutput(item);
                                    if (recipes.isEmpty()) {
                                        context.getSource().sendFailure(Component.literal("No recipes found for %s".formatted(item.getDescriptionId())));
                                        return 0;
                                    }
                                    List<MutableComponent> texts = recipes.stream()
                                            .map(ResourceKey::identifier)
                                            .map(identifier -> Component.literal(identifier.toString()))
                                            .toList();
                                    context.getSource().sendSuccess(
                                            () -> Component.literal("Recipes for ")
                                                    .append(item.getName(item.getDefaultInstance()))
                                                    .append(Component.literal(":"))
                                                    .append(CommonComponents.NEW_LINE)
                                                    .append(ComponentUtils.formatList(texts, CommonComponents.NEW_LINE)),
                                            true
                                    );
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("test")
                        .then(Commands.literal("drops")
                                .executes(context -> {
                                    context.getSource()
                                            .sendSuccess(() -> Component.literal("Triggering all loot tables, world will lag for a bit"), true);
                                    ServerPlayer player = context.getSource()
                                            .getPlayer();
                                    assert player != null;
                                    ServerLevel level = player.level();
                                    BlockPos up = player.blockPosition().offset(0, 20, 0);
                                    BuiltInRegistries.BLOCK.forEach((block -> {
                                        level.setBlock(up, block.defaultBlockState(), 0);
                                        level.destroyBlock(up, true, player);
                                    }));
                                    BuiltInRegistries.ENTITY_TYPE.forEach((entityType -> {
                                        Entity entity = entityType.create(level, EntitySpawnReason.COMMAND);
                                        if (!(entity instanceof LivingEntity)) {
                                            return;
                                        }
                                        entity.teleportTo(player.getX(), player.getY() + 1, player.getZ());
                                        level.addFreshEntity(entity);
                                        entity.hurtServer(level, level.damageSources().playerAttack(player), Float.MAX_VALUE);
                                    }));
                                    return 1;
                                })
                        )
                        .then(Commands.literal("newseed")
                                .executes(context -> {
                                    context.getSource()
                                            .sendSuccess(() -> Component.literal("Unimplemented"), true);
                                    return 1;
                                })
                        )
                )
                .executes(context -> execute(context, Selection.SELF))
        );
    }

    private int execute(CommandContext<CommandSourceStack> context, Selection selection) {
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

        public void register() {
            PostExecuteCallback.EVENT.register(this);
        }

        @Override
        public void postExecute(CommandSourceStack source) throws CommandSyntaxException {
            if (runnable != null) {
                runnable.run();
                runnable = null;
            }
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
