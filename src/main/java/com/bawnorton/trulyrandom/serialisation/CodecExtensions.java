package com.bawnorton.trulyrandom.serialisation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.Objects;
import java.util.Optional;

public interface CodecExtensions {
    static <A> MapCodec<A> fwOptionalFieldOf(Codec<A> elementCodec, String name, A defaultValue) {
        return fwOptionalField(elementCodec, name, defaultValue).xmap(
                o -> o.orElse(defaultValue),
                a -> Objects.equals(a, defaultValue) ? Optional.empty() : Optional.of(a)
        );
    }

    static <A> MapCodec<Optional<A>> fwOptionalField(Codec<A> elementCodec, String name, A defaultValue) {
        return new ForceWriteOptionalFieldCodec<>(name, elementCodec, defaultValue);
    }
}
