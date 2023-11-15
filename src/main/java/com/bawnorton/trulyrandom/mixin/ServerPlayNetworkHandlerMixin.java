package com.bawnorton.trulyrandom.mixin;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.listener.TrulyRandomServerPlayPacketListener;
import com.bawnorton.trulyrandom.network.packet.c2s.HandshakeC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.SyncRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.HandshakeS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.ShuffleModelsS2CPacket;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandler implements TrulyRandomServerPlayPacketListener {
    @Shadow
    public ServerPlayerEntity player;

    protected ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Override
    public void trulyRandom$onSyncRandomiser(SyncRandomiserC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, player.getServerWorld());

        Randomiser randomiser = TrulyRandom.getRandomiser(server);
        randomiser.setModules(packet.modules());
        boolean seedChanged = randomiser.getSeed() != packet.seed();
        if(seedChanged) {
            randomiser.newSessionRandom(packet.seed());
            randomiser.shouldRandomiseLoot(() -> randomiser.randomiseLoot(server), () -> randomiser.getLootRandomiser().reset(server));
        }
        randomiser.shouldShuffleModels((items, blocks) -> sendPacket(new ShuffleModelsS2CPacket(items, blocks, seedChanged, randomiser.getSeed())));
    }

    @Override
    public void trulyRandom$onHandshake(HandshakeC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, this, player.getServerWorld());

        Randomiser randomiser = TrulyRandom.getRandomiser(server);
        sendPacket(new HandshakeS2CPacket(TrulyRandom.VERSION, randomiser.getModules(), randomiser.getSeed()));
    }
}
