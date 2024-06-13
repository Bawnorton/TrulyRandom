package com.bawnorton.trulyrandom.command;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandHandler {
    private static final TrulyRandomSettingsCommand trulyRandomSettingsCommand;

    static {
        trulyRandomSettingsCommand = new TrulyRandomSettingsCommand();
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        trulyRandomSettingsCommand.register(dispatcher);
        dispatcher.register(CommandManager.literal("mine_all_blocks").executes(context -> {
            PlayerEntity player = context.getSource().getPlayer();
            if(player == null) return 1;

            BlockPos pos = player.getBlockPos().up(3);
            World world = player.getWorld();
            Registries.BLOCK.forEach(block -> {
               world.setBlockState(pos, block.getDefaultState(), 0);
               world.breakBlock(pos, true, player, 0);
            });
            LootTableTracker tracker = TrulyRandom.getRandomiser(world.getServer()).getLootRandomiser().getTracker(player);
            DataResult<JsonElement> jsonResult = LootTableTracker.CODEC.encodeStart(JsonOps.INSTANCE, tracker);
            jsonResult.ifSuccess(json -> TrulyRandom.LOGGER.info(json.toString()));
            return 0;
        }));
    }
}
