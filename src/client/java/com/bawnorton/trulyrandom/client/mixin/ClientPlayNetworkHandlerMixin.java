package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.network.ClientNetworking;
import com.bawnorton.trulyrandom.network.listener.TrulyRandomClientPlayPacketListener;
import com.bawnorton.trulyrandom.network.packet.c2s.SyncRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.HandshakeS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.ShuffleModelsS2CPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Random;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler implements TrulyRandomClientPlayPacketListener {
    @Unique
    private boolean lastItems = false;
    @Unique
    private boolean lastBlocks = false;
    @Unique
    private boolean forceRandomise = true;

    public ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
        super(client, clientConnection, clientConnectionState);
    }

    @Override
    public void trulyRandom$onShuffleModels(ShuffleModelsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, client);

        ClientNetworking.setLocalSeed(packet.seed());
        if(packet.seedChanged()) forceRandomise = true;
        if (forceRandomise || packet.items() ^ lastItems) {
            if (packet.items()) {
                ((ModelShuffler.Items) client.getItemRenderer().getModels()).trulyrandom$shuffleModels(new Random(packet.seed()));
            } else {
                ((ModelShuffler.Items) client.getItemRenderer().getModels()).trulyrandom$resetModels();
            }
            client.getItemRenderer().getModels().reloadModels();
        }
        if (forceRandomise || packet.blocks() ^ lastBlocks) {
            if (packet.blocks()) {
                ((ModelShuffler.BlockStates) client.getBakedModelManager().getBlockModels()).trulyrandom$shuffleModels(new Random(packet.seed()));
            } else {
                ((ModelShuffler.BlockStates) client.getBakedModelManager().getBlockModels()).trulyrandom$resetModels();
            }
            client.worldRenderer.reload();
        }
        lastItems = packet.items();
        lastBlocks = packet.blocks();
        forceRandomise = false;
    }

    @Override
    public void trulyRandom$onHandshake(HandshakeS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, client);

        ClientNetworking.setLocalSeed(packet.seed());
        ClientNetworking.setServerVersion(packet.version());
        sendPacket(new SyncRandomiserC2SPacket(packet.modules(), packet.seed()));
    }
}
