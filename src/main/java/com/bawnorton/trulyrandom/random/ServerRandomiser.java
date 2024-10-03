package com.bawnorton.trulyrandom.random;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.packet.s2c.SetClientRandomiserS2CPacket;
import com.bawnorton.trulyrandom.random.loot.LootRandomiser;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import com.bawnorton.trulyrandom.random.recipe.RecipeRandomiser;
import com.bawnorton.trulyrandom.random.trade.TradeRandomiser;
import com.bawnorton.trulyrandom.tracker.Tracker;
import com.bawnorton.trulyrandom.tracker.difficulty.DifficultyCalculator;
import com.bawnorton.trulyrandom.tracker.difficulty.DifficultyRating;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class ServerRandomiser extends Randomiser {
    private LootRandomiser lootRandomiser;
    private RecipeRandomiser recipeRandomiser;
    private TradeRandomiser tradeRandomiser;
    private DifficultyCalculator difficultyCalculator;

    private boolean initialised = false;
    private NbtCompound lootRandomiserData;

    public ServerRandomiser(@NotNull Modules modules) {
        super(modules);
    }

    private ServerRandomiser() {
        this(new Modules());
    }

    public static ServerRandomiser fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        ServerRandomiser randomiser = new ServerRandomiser();
        randomiser.readNbt(nbt, lookup);
        return randomiser;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("loot_randomiser", lootRandomiser.writeNbt(new NbtCompound()));
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        super.readNbt(nbt, lookup);
        lootRandomiserData = nbt.getCompound("loot_randomiser");
    }

    public void init(MinecraftServer server) {
        initialised = true;
        this.lootRandomiser = new LootRandomiser(server);
        this.recipeRandomiser = new RecipeRandomiser(server);
        this.tradeRandomiser = new TradeRandomiser();
        this.difficultyCalculator = new DifficultyCalculator();

        if (lootRandomiserData != null) {
            lootRandomiser.readNbt(lootRandomiserData);
        }
    }

    public boolean initialised() {
        return initialised;
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

    public DifficultyRating calculateDifficulty() {
        return difficultyCalculator.calculateDifficulty(lootRandomiser, recipeRandomiser);
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

    private void update(ServerRandomiserModule<?, ?> randomiser, MinecraftServer server, boolean seedChanged) {
        update(randomiser, server, seedChanged, false);
    }

    private void update(ServerRandomiserModule<?, ?> randomiser, MinecraftServer server, boolean seedChanged, boolean force) {
        if (!initialised) throw new IllegalStateException("Randomiser not initialised");

        boolean moduleEnabled = modules.isEnabled(randomiser.getModule());
        boolean isRandomised = randomiser.isRandomised();

        if(!moduleEnabled) {
            if(isRandomised) {
                randomiser.reset(server);
                randomiser.setRandomised(false);
                randomiser.getTrackers().forEach(Tracker::reset);
            }
        } else if (!isRandomised || seedChanged || force) {
            randomiser.randomise(server, modules.getSeed(randomiser.getModule()));
            randomiser.setRandomised(true);
            if(seedChanged) {
                randomiser.getTrackers().forEach(Tracker::reset);
            }
        }
    }
}
