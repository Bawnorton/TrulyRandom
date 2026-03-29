package com.bawnorton.trulyrandom.command.argument;

import com.google.gson.JsonObject;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.List;

public class SetStringArgumentTypeSerializer implements ArgumentTypeInfo<SetStringArgumentType, SetStringArgumentTypeSerializer.Properties> {

    @Override
    public void serializeToNetwork(Properties properties, FriendlyByteBuf buf) {
        buf.writeCollection(properties.options, FriendlyByteBuf::writeUtf);
    }

    @Override
    public Properties deserializeFromNetwork(FriendlyByteBuf buf) {
        List<String> options = buf.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);
        return new Properties(options);
    }

    @Override
    public void serializeToJson(Properties properties, JsonObject json) {
        json.addProperty("options", String.join(",", properties.options));
    }

    @Override
    public Properties unpack(SetStringArgumentType argumentType) {
        return new Properties(argumentType.options());
    }

    public final class Properties implements ArgumentTypeInfo.Template<SetStringArgumentType> {
        private final List<String> options;

        public Properties(List<String> options) {
            this.options = options;
        }

        @Override
        public SetStringArgumentType instantiate(CommandBuildContext context) {
            return new SetStringArgumentType(options);
        }

        @Override
        public ArgumentTypeInfo<SetStringArgumentType, ?> type() {
            return SetStringArgumentTypeSerializer.this;
        }
    }
}