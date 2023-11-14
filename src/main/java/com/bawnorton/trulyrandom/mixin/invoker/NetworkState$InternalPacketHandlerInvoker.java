package com.bawnorton.trulyrandom.mixin.invoker;

import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

@Mixin(NetworkState.InternalPacketHandler.class)
public interface NetworkState$InternalPacketHandlerInvoker {
    @Invoker
    NetworkState.InternalPacketHandler<?> invokeRegister(Class<?> type, Function<PacketByteBuf, ?> packetFactory);
}
