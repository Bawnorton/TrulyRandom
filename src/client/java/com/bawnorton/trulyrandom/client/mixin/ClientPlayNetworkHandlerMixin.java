package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.network.ClientNetworking;
import com.bawnorton.trulyrandom.client.random.ClientRandomiser;
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

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler implements TrulyRandomClientPlayPacketListener {
    @Unique
    private boolean forceRandomise = true;

    public ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
        super(client, clientConnection, clientConnectionState);
    }

    @Override
    public void trulyRandom$onShuffleModels(ShuffleModelsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, client);

        ClientRandomiser randomiser = TrulyRandomClient.getRandomiser();
        randomiser.setLocalSeed(packet.seed());
        if (packet.seedChanged()) forceRandomise = true;
        randomiser.updateItemModels(client, packet.items(), forceRandomise);
        randomiser.updateBlockModels(client, packet.blocks(), forceRandomise);
        forceRandomise = false;
    }

    @Override
    public void trulyRandom$onHandshake(HandshakeS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, client);

        TrulyRandomClient.getRandomiser().setLocalSeed(packet.seed());
        ClientNetworking.setServerVersion(packet.version());
        sendPacket(new SyncRandomiserC2SPacket(packet.modules(), packet.seed()));
    }
}
