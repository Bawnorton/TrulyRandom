package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.network.listener.TrulyRandomClientPlayPacketListener;
import com.bawnorton.trulyrandom.network.packet.ShuffleModelsS2CPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler implements TrulyRandomClientPlayPacketListener {
    public ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection clientConnection, ClientConnectionState clientConnectionState) {
        super(client, clientConnection, clientConnectionState);
    }

    @Override
    public void trulyRandom$onShuffleModels(ShuffleModelsS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, client);
        if(packet.items()) {
            ((ModelShuffler) client.getBakedModelManager()).trulyrandom$shuffleModels(TrulyRandom.getRandomiser().getSessionRandom());
            client.getItemRenderer().getModels().reloadModels();
        }
        if(packet.blocks()) {
            ((ModelShuffler) client.getBakedModelManager().getBlockModels()).trulyrandom$shuffleModels(TrulyRandom.getRandomiser().getSessionRandom());
            client.worldRenderer.reload();
        }
    }
}
