package com.bawnorton.trulyrandom.client.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public record BlockStateVariant(BlockState state) {
    private static final Function<Map.Entry<Property<?>, Comparable<?>>, String> PROPERTY_MAP_PRINTER = new Function<>() {
        public String apply(@Nullable Map.Entry<Property<?>, Comparable<?>> entry) {
            if (entry == null) {
                return "<NULL>";
            } else {
                Property<?> property = entry.getKey();
                return property.getName() + "=" + this.nameValue(property, entry.getValue());
            }
        }

        @SuppressWarnings("unchecked")
        private <T extends Comparable<T>> String nameValue(Property<T> property, Comparable<?> value) {
            return property.name((T) value);
        }
    };

    public String variant() {
        return state.getEntries().entrySet().stream().map(PROPERTY_MAP_PRINTER).collect(Collectors.joining(",")) +
                "," + state.getOutlineShape(MinecraftClient.getInstance().world, new BlockPos(0, 300, 0)) +
                "," + state.getCullingShape(MinecraftClient.getInstance().world, new BlockPos(0, 300, 0));
    }
}