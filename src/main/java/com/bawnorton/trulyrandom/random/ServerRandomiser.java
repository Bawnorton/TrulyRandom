package com.bawnorton.trulyrandom.random;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.packet.s2c.SetClientRandomiserS2CPacket;
import com.bawnorton.trulyrandom.random.loot.LootRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import com.bawnorton.trulyrandom.random.recipe.RecipeRandomiser;
import com.bawnorton.trulyrandom.random.trade.TradeRandomiser;
import com.bawnorton.trulyrandom.tracker.Tracker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.NotNull;

public class ServerRandomiser extends Randomiser {
    private final LootRandomiser lootRandomiser;
    private final RecipeRandomiser recipeRandomiser;
    private final TradeRandomiser tradeRandomiser;

    public ServerRandomiser(@NotNull Modules modules, MinecraftServer server) {
        super(modules);
        this.lootRandomiser = new LootRandomiser(server);
        this.recipeRandomiser = new RecipeRandomiser(server);
        this.tradeRandomiser = new TradeRandomiser();
        this.recipeRandomiser.setOriginalOutputs(server, modules.getSeed(Module.RECIPES));
    }

    private ServerRandomiser(Modules modules, MinecraftServer server, LootRandomiser lootRandomiser, RecipeRandomiser recipeRandomiser) {
        super(modules);
        this.lootRandomiser = lootRandomiser;
        this.recipeRandomiser = recipeRandomiser;
        this.tradeRandomiser = new TradeRandomiser();
        this.recipeRandomiser.setOriginalOutputs(server, modules.getSeed(Module.RECIPES));
    }

    public static Codec<ServerRandomiser> codec(PersistentState.Context context) {
        return RecordCodecBuilder.create(instance -> instance.group(
                Modules.CODEC.fieldOf("modules").forGetter(Randomiser::getModules),
                LootRandomiser.codec(context).fieldOf("loot_randomiser").forGetter(ServerRandomiser::getLootRandomiser),
                RecipeRandomiser.codec(context).fieldOf("recipe_randomiser").forGetter(ServerRandomiser::getRecipeRandomiser)
        ).apply(instance, (modules, lootRandomiser, recipeRandomiser) -> new ServerRandomiser(modules, context.getWorldOrThrow().getServer(), lootRandomiser, recipeRandomiser)));
    }

    public LootRandomiser getLootRandomiser() {
        return lootRandomiser;
    }

    public RecipeRandomiser getRecipeRandomiser() {
        return recipeRandomiser;
    }

    public TradeRandomiser getTradeRandomiser() {
        return tradeRandomiser;
    }

    public void updateLoot(MinecraftServer server, boolean seedChanged) {
        update(lootRandomiser, server, seedChanged);
    }

    public void updateRecipes(MinecraftServer server, boolean seedChanged) {
        updateRecipes(server, seedChanged, false);
    }

    public void updateRecipes(MinecraftServer server, boolean seedChanged, boolean force) {
        update(recipeRandomiser, server, seedChanged, force);
    }

    public void updateTrades(MinecraftServer server, boolean seedChanged) {
        update(tradeRandomiser, server, seedChanged);
    }

    public void updateClients(MinecraftServer server) {
        server.getPlayerManager()
                .getPlayerList()
                .forEach(player -> {
                    TrulyRandom.setClientRandomiser(player.getServer(), player.getUuid(), modules);
                    ServerPlayNetworking.send(player, new SetClientRandomiserS2CPacket(modules));
                });
    }

    private void update(ServerRandomiserModule randomiser, MinecraftServer server, boolean seedChanged) {
        update(randomiser, server, seedChanged, false);
    }

    private void update(ServerRandomiserModule randomiser, MinecraftServer server, boolean seedChanged, boolean force) {
        boolean moduleEnabled = modules.isEnabled(randomiser.getModule());
        boolean isRandomised = randomiser.isRandomised();

        if(!moduleEnabled) {
            if(isRandomised) {
                randomiser.reset(server);
                randomiser.setRandomised(false);
                randomiser.getTrackerList().forEach(Tracker::reset);
            }
        } else if (!isRandomised || seedChanged || force) {
            randomiser.randomise(server, modules.getSeed(randomiser.getModule()));
            randomiser.setRandomised(true);
            if(seedChanged) {
                randomiser.getTrackerList().forEach(Tracker::reset);
            }
        }
    }
}
