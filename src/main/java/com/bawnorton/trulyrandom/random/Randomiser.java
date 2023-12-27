package com.bawnorton.trulyrandom.random;

import com.bawnorton.trulyrandom.network.packet.s2c.ShuffleModelsS2CPacket;
import com.bawnorton.trulyrandom.random.loot.LootRandomiser;
import com.bawnorton.trulyrandom.random.recipe.RecipeRandomiser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;

import java.util.Random;
import java.util.function.Consumer;

public class Randomiser {
    public static final Codec<Randomiser> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("seed").forGetter(Randomiser::getSeed),
            Modules.CODEC.fieldOf("modules").forGetter(Randomiser::getModules)
    ).apply(instance, Randomiser::new));

    public static final Randomiser DEFAULT = new Randomiser();

    private Modules modules;
    private long seed;
    private boolean initialised = false;
    private ServerRandomiserModule lootRandomiser;
    private ServerRandomiserModule recipeRandomiser;

    private Randomiser(long seed, Modules modules) {
        this.seed = seed;
        this.modules = modules;
    }

    public Randomiser(long seed) {
        this(seed, new Modules());
    }

    private Randomiser() {
        this(new Random().nextLong());
    }

    public void init(MinecraftServer server) {
        initialised = true;
        this.lootRandomiser = new LootRandomiser(server);
        this.recipeRandomiser = new RecipeRandomiser(server);
    }

    public boolean initialised() {
        return initialised;
    }

    public static Randomiser fromNbt(NbtCompound nbt) {
        Randomiser randomiser = new Randomiser(nbt.getLong("seed"));
        randomiser.modules = new Modules();
        randomiser.modules.readNbt(nbt.getCompound("modules"));
        return randomiser;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putLong("seed", seed);
        NbtCompound modulesNbt = new NbtCompound();
        modules.writeNbt(modulesNbt);
        nbt.put("modules", modulesNbt);
        return nbt;
    }

    public long getSeed() {
        return seed;
    }

    public Modules getModules() {
        return modules;
    }

    public void setModules(Modules modules) {
        this.modules = modules;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void updateLoot(MinecraftServer server, boolean seedChanged) {
        update(lootRandomiser, server, seedChanged);
    }

    public void updateRecipes(MinecraftServer server, boolean seedChanged) {
        update(recipeRandomiser, server, seedChanged);
    }

    private void update(ServerRandomiserModule randomiser, MinecraftServer server, boolean seedChanged) {
        if(!initialised) throw new IllegalStateException("Randomiser not initialised");

        if(modules.isEnabled(randomiser.getModule()) && !randomiser.isRandomised() || (randomiser.isRandomised() && seedChanged)) {
            randomiser.randomise(server, seed);
        } else if (modules.isDisabled(randomiser.getModule()) && randomiser.isRandomised()) {
            randomiser.reset(server);
        }
    }

    public void sendModelShufflePacket(Consumer<Packet<?>> sender, boolean seedChanged) {
        sender.accept(new ShuffleModelsS2CPacket(modules.isEnabled(Module.ITEM_MODELS), modules.isEnabled(Module.BLOCK_MODELS), seedChanged, seed));
    }
}
