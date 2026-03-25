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
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.BuiltInRegistries;
import net.minecraft.registry.ResourceKey;
import net.minecraft.registry.entry.Holder;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.LootCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import java.util.Optional;

public class TrulyRandomCommand {
    private final PostExecuteRunner runner;

    public TrulyRandomCommand() {
        runner = new PostExecuteRunner();
        runner.register();
    }

    private static void executeOpenRandomiserScreen(CommandContext<ServerCommandSource> context, Selection selection) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayer executor = source.getPlayer();
        if (executor == null) {
            source.sendFeedback(() -> Text.literal("You must be a player to use this command"), true);
            return;
        }

        ServerPlayer target = null;
        if (selection == Selection.PLAYER) {
            target = EntityArgumentType.getPlayer(context, "player");
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

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(CommandManager.literal("trulyrandom")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("settings")
                        .then(CommandManager.argument("selection", SetStringArgumentType.of("server", "all"))
                                .executes(context -> execute(context, Selection.SERVER))
                        )
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> execute(context, Selection.PLAYER))
                        )
                )
                .then(CommandManager.literal("drop")
                        .then(CommandManager.argument("loot_table", RegistryEntryArgumentType.lootTable(commandRegistryAccess))
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerRandomiser randomiser = TrulyRandom.getRandomiser(source.getServer());
                                    Holder<LootTable> lootTable = RegistryEntryArgumentType.LootTableArgumentType.getLootTable(context, "loot_table");
                                    Optional<ResourceKey<LootTable>> keyOptional = lootTable.getKey();
                                    if (keyOptional.isEmpty()) {
                                        context.getSource().sendError(Text.of("Could not find loot table key for \"%s\"".formatted(lootTable.getIdAsString())));
                                        return 0;
                                    }
                                    String to = lootTable.getIdAsString();
                                    String from = randomiser.getLootRandomiser().getSourceTable(keyOptional.orElseThrow()).getValue().toString();
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("%s".formatted(to))
                                                    .append(ScreenTexts.LINE_BREAK)
                                                    .append("drops from")
                                                    .append(ScreenTexts.LINE_BREAK)
                                                    .append("%s".formatted(from)),
                                            true
                                    );
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("recipe")
                        .then(CommandManager.argument("recipe", ItemStackArgumentType.itemStack(commandRegistryAccess))
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    ServerRandomiser randomiser = TrulyRandom.getRandomiser(source.getServer());
                                    ItemStackArgument itemStackArgument = ItemStackArgumentType.getItemStackArgument(context, "recipe");
                                    Item item = itemStackArgument.getItem();
                                    List<ResourceKey<Recipe<?>>> recipes = randomiser.getRecipeRandomiser().getRecipesForOutput(item);
                                    if (recipes.isEmpty()) {
                                        context.getSource().sendError(Text.of("No recipes found for %s".formatted(item.getTranslationKey())));
                                        return 0;
                                    }
                                    List<Text> texts = recipes.stream()
                                            .map(ResourceKey::getValue)
                                            .map(identifier -> Text.of(identifier.toString()))
                                            .toList();
                                    context.getSource().sendFeedback(
                                            () -> Text.literal("Recipes for ")
                                                    .append(item.getName())
                                                    .append(Text.literal(":"))
                                                    .append(ScreenTexts.LINE_BREAK)
                                                    .append(Texts.join(texts, ScreenTexts.LINE_BREAK)),
                                            true
                                    );
                                    return 1;
                                })
                        )
                )
                .then(CommandManager.literal("test")
                        .then(CommandManager.literal("drops")
                                .executes(context -> {
                                    context.getSource()
                                            .sendFeedback(() -> Text.of("Triggering all loot tables, world will lag for a bit"), true);
                                    ServerPlayer player = context.getSource()
                                            .getPlayer();
                                    assert player != null;
                                    ServerWorld world = player.getWorld();
                                    BlockPos up = player.getBlockPos()
                                            .add(0, 20, 0);
                                    BuiltInRegistries.BLOCK.forEach((block -> {
                                        world.setBlockState(up, block.getDefaultState(), 0);
                                        world.breakBlock(up, true, player);
                                    }));
                                    BuiltInRegistries.ENTITY_TYPE.forEach((entityType -> {
                                        Entity entity = entityType.create(world, SpawnReason.COMMAND);
                                        if (!(entity instanceof LivingEntity)) {
                                            return;
                                        }
                                        entity.updatePosition(player.getX(), player.getY() + 1, player.getZ());
                                        world.spawnEntity(entity);
                                        entity.damage(world, world.getDamageSources().playerAttack(player), Float.MAX_VALUE);
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
                .executes(context -> execute(context, Selection.SELF))
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

        public void register() {
            PostExecuteCallback.EVENT.register(this);
        }

        @Override
        public void postExecute(ServerCommandSource source) throws CommandSyntaxException {
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
