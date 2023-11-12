package com.bawnorton.trulyrandom.random;

import com.bawnorton.trulyrandom.network.Networking;
import com.bawnorton.trulyrandom.network.packet.ShuffleModelsS2CPacket;

import java.util.Random;

public class Randomiser {
    private long seed;
    private Random sessionRandom;
    private Modules modules;

    public Randomiser(long seed) {
        this.seed = seed;
        this.sessionRandom = new Random(seed);
    }

    public Randomiser() {
        this(new Random().nextLong());
    }

    public long getSeed() {
        return seed;
    }

    public void setModules(Modules modules) {
        this.modules = modules;
    }

    public void newSessionRandom(long seed) {
        this.seed = seed;
        sessionRandom = new Random(seed);
    }

    public void newSessionRandom() {
        newSessionRandom(new Random().nextLong());
    }

    public Random getSessionRandom() {
        return sessionRandom;
    }

    public int nextInt(int bound) {
        return sessionRandom.nextInt(bound);
    }

    public void shuffleItemModels() {
        if(modules.isDisabled(Module.ITEM_MODELS)) return;

        Networking.sendToAllPlayers(new ShuffleModelsS2CPacket(true, false));
    }

    public void shuffleBlockModels() {
        if(modules.isDisabled(Module.BLOCK_MODELS)) return;

        Networking.sendToAllPlayers(new ShuffleModelsS2CPacket(false, true));
    }

    public void shuffleModels() {
        if(modules.isDisabled(Module.ITEM_MODELS) && modules.isDisabled(Module.BLOCK_MODELS)) return;

        Networking.sendToAllPlayers(new ShuffleModelsS2CPacket(modules.isEnabled(Module.ITEM_MODELS), modules.isEnabled(Module.BLOCK_MODELS)));
    }
}
