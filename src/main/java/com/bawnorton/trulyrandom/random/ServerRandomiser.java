package com.bawnorton.trulyrandom.random;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.packet.s2c.SetClientRandomiserS2CPacket;
import com.bawnorton.trulyrandom.random.loot.LootRandomiser;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import com.bawnorton.trulyrandom.random.recipe.RecipeRandomiser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class ServerRandomiser extends Randomiser {
    public static final Codec<Randomiser> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Modules.CODEC.fieldOf("modules").forGetter(Randomiser::getModules)
    ).apply(instance, ServerRandomiser::new));

    public static final ServerRandomiser DEFAULT = new ServerRandomiser();

    private LootRandomiser lootRandomiser;
    private RecipeRandomiser recipeRandomiser;

    private boolean initialised = false;

    public ServerRandomiser(@NotNull Modules modules) {
        super(modules);
    }

    private ServerRandomiser() {
        this(new Modules());
    }

    public static ServerRandomiser fromNbt(NbtCompound nbt) {
        ServerRandomiser randomiser = new ServerRandomiser();
        randomiser.readNbt(nbt);
        return randomiser;
    }

    public void init(MinecraftServer server) {
        initialised = true;
        this.lootRandomiser = new LootRandomiser(server);
        this.recipeRandomiser = new RecipeRandomiser(server);
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

    public void updateLoot(MinecraftServer server, boolean seedChanged) {
        update(lootRandomiser, server, seedChanged);
    }

    public void updateRecipes(MinecraftServer server, boolean seedChanged) {
        update(recipeRandomiser, server, seedChanged);
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
        if (!initialised) throw new IllegalStateException("Randomiser not initialised");

        boolean moduleEnabled = modules.isEnabled(randomiser.getModule());
        boolean isRandomised = randomiser.isRandomised();

        if(!moduleEnabled) {
            if(isRandomised) {
                randomiser.reset(server);
                randomiser.setRandomised(false);
            }
        } else if (!isRandomised || seedChanged) {
            randomiser.randomise(server, modules.getSeed(randomiser.getModule()));
            randomiser.setRandomised(true);
        }
    }
}
