package com.bawnorton.trulyrandom.command.argument;

import com.google.gson.JsonObject;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public class SetStringArgumentTypeSerializer implements ArgumentSerializer<SetStringArgumentType, SetStringArgumentTypeSerializer.Properties> {

    @Override
    public void writePacket(Properties properties, PacketByteBuf buf) {
        buf.writeCollection(properties.options, PacketByteBuf::writeString);
    }

    @Override
    public Properties fromPacket(PacketByteBuf buf) {
        List<String> options = buf.readCollection(ArrayList::new, PacketByteBuf::readString);
        return new Properties(options);
    }

    @Override
    public void writeJson(Properties properties, JsonObject json) {
        json.addProperty("options", String.join(",", properties.options));
    }

    @Override
    public Properties getArgumentTypeProperties(SetStringArgumentType argumentType) {
        return new Properties(argumentType.options);
    }

    public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<SetStringArgumentType> {
        private final List<String> options;

        public Properties(List<String> options) {
            this.options = options;
        }

        public SetStringArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new SetStringArgumentType(options);
        }

        @Override
        public ArgumentSerializer<SetStringArgumentType, ?> getSerializer() {
            return SetStringArgumentTypeSerializer.this;
        }
    }
}