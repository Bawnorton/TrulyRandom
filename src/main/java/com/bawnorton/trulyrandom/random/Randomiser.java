package com.bawnorton.trulyrandom.random;

import com.bawnorton.trulyrandom.network.packet.s2c.ShuffleModelsS2CPacket;
import com.bawnorton.trulyrandom.random.loot.LootRandomiser;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;

import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Randomiser {
    private LootRandomiser lootRandomiser;
    private Random sessionRandom;
    private Modules modules;
    private long seed;
    private boolean initialised = false;

    public Randomiser(long seed) {
        this.sessionRandom = new Random(seed);
        this.modules = new Modules();
        this.seed = seed;
    }

    public void init(MinecraftServer server) {
        this.lootRandomiser = new LootRandomiser(server);
        initialised = true;
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

    public void newSessionRandom(long seed) {
        this.seed = seed;
        sessionRandom = new Random(seed);
    }

    public void updateLoot(MinecraftServer server) {
        if(!initialised) throw new IllegalStateException("Randomiser not initialised");
        if(modules.isEnabled(Module.LOOT_TABLES)) {
            lootRandomiser.randomiseLoot(server, sessionRandom);
        }
        else lootRandomiser.reset(server);
    }

    public void sendModelShufflePacket(Consumer<Packet<?>> sender, boolean seedChanged) {
        sender.accept(new ShuffleModelsS2CPacket(modules.isEnabled(Module.ITEM_MODELS), modules.isEnabled(Module.BLOCK_MODELS), seedChanged, seed));
    }
}
