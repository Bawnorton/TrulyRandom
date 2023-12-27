package com.bawnorton.trulyrandom.mixin;

import com.bawnorton.trulyrandom.mixin.invoker.NetworkState$InternalPacketHandlerInvoker;
import com.bawnorton.trulyrandom.network.packet.c2s.HandshakeC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.SyncRandomiserC2SPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.HandshakeS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.ShuffleModelsS2CPacket;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(NetworkState.class)
public abstract class NetworkStateMixin {
    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/NetworkState$PacketHandlerInitializer;setup(Lnet/minecraft/network/NetworkSide;Lnet/minecraft/network/NetworkState$InternalPacketHandler;)Lnet/minecraft/network/NetworkState$PacketHandlerInitializer;",
                    ordinal = 0
            ),
            index = 1,
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "stringValue=play"
                    )
            )
    )
    private static <T extends PacketListener> NetworkState.InternalPacketHandler<T> trulyRandom$registerS2CPackets(NetworkState.InternalPacketHandler<T> original) {
        NetworkState$InternalPacketHandlerInvoker invoker = (NetworkState$InternalPacketHandlerInvoker) original;
        invoker.invokeRegister(HandshakeS2CPacket.class, HandshakeS2CPacket::new);
        invoker.invokeRegister(ShuffleModelsS2CPacket.class, ShuffleModelsS2CPacket::new);
        return original;
    }

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/NetworkState$PacketHandlerInitializer;setup(Lnet/minecraft/network/NetworkSide;Lnet/minecraft/network/NetworkState$InternalPacketHandler;)Lnet/minecraft/network/NetworkState$PacketHandlerInitializer;",
                    ordinal = 1
            ),
            index = 1,
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "stringValue=play"
                    )
            )
    )
    private static <T extends PacketListener> NetworkState.InternalPacketHandler<T> trulyRandom$registerC2SPackets(NetworkState.InternalPacketHandler<T> original) {
        NetworkState$InternalPacketHandlerInvoker invoker = (NetworkState$InternalPacketHandlerInvoker) original;
        invoker.invokeRegister(SyncRandomiserC2SPacket.class, SyncRandomiserC2SPacket::new);
        invoker.invokeRegister(HandshakeC2SPacket.class, HandshakeC2SPacket::new);
        return original;
    }
}
