package com.bawnorton.trulyrandom.serialisation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.OptionalFieldCodec;

import java.util.Optional;

public class ForceWriteOptionalFieldCodec<A> extends OptionalFieldCodec<A> {
    private final String name;
    private final Codec<A> elementCodec;
    private final A defaultValue;

    public ForceWriteOptionalFieldCodec(String name, Codec<A> elementCodec, A defaultValue) {
        super(name, elementCodec, false);
        this.name = name;
        this.elementCodec = elementCodec;
        this.defaultValue = defaultValue;
    }

    @Override
    public <T> RecordBuilder<T> encode(Optional<A> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        return prefix.add(name, elementCodec.encodeStart(ops, input.orElse(defaultValue)));
    }
}
