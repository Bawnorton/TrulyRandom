package com.bawnorton.trulyrandom.command.argument;

import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public class SetStringArgumentTypeSerializer implements ArgumentTypeInfo<SetStringArgumentType, SetStringArgumentTypeSerializer.Properties> {
    public static final SetStringArgumentTypeSerializer INSTANCE = new SetStringArgumentTypeSerializer();

    @Override
    public void serializeToNetwork(Properties properties, FriendlyByteBuf buf) {
        Properties.STREAM_CODEC.encode(buf, properties);
    }

    @Override
    public Properties deserializeFromNetwork(FriendlyByteBuf buf) {
        return Properties.STREAM_CODEC.decode(buf);
    }

    @Override
    public void serializeToJson(Properties properties, JsonObject json) {
        json.addProperty("options", String.join(",", properties.options));
    }

    @Override
    public Properties unpack(SetStringArgumentType argumentType) {
        return new Properties(argumentType.options());
    }

    public record Properties(List<String> options) implements ArgumentTypeInfo.Template<SetStringArgumentType> {
        public static final StreamCodec<ByteBuf, Properties> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8),
                Properties::options,
                Properties::new
        );

        @Override
        public SetStringArgumentType instantiate(CommandBuildContext context) {
            return new SetStringArgumentType(options);
        }

        @Override
        public ArgumentTypeInfo<SetStringArgumentType, ?> type() {
            return INSTANCE;
        }
    }
}