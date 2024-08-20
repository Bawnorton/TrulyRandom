package com.bawnorton.trulyrandom.random.module;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Modules implements Iterable<Module> {
    public static final Codec<Modules> CODEC = Codec.unboundedMap(Module.CODEC, ModuleState.CODEC)
            .xmap(Modules::new, modules -> modules.moduleStates);
    public static final PacketCodec<ByteBuf, Modules> PACKET_CODEC = PacketCodecs.map(HashMap::new, Module.PACKET_CODEC, ModuleState.PACKET_CODEC)
            .xmap(Modules::new, modules -> new HashMap<>(modules.moduleStates));

    private final Map<Module, ModuleState> moduleStates;
    private final Map<Module, Boolean> enabledMemento = new HashMap<>();
    private final Map<Module, Long> seedMemento = new HashMap<>();

    public Modules() {
        moduleStates = new HashMap<>();
        for (Module module : Module.values()) {
            moduleStates.put(module, new ModuleState());
        }
    }

    private Modules(Map<Module, ModuleState> moduleStates) {
        this.moduleStates = moduleStates;
    }

    public static Modules fromNbt(NbtCompound nbt) {
        Modules modules = new Modules();
        modules.readNbt(nbt);
        return modules;
    }

    public boolean isEnabled(Module module) {
        return moduleStates.get(module).isEnabled();
    }

    public boolean isVisible(Module module) {
        return moduleStates.get(module).isVisible();
    }

    public long getSeed(Module module) {
        return moduleStates.get(module).getSeed();
    }

    public void hideServerSide() {
        moduleStates.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isServerSide())
                .forEach(entry -> entry.getValue().hide());
    }

    public void showServerSide() {
        moduleStates.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isServerSide())
                .forEach(entry -> entry.getValue().show());
    }

    public boolean getEnabledMemento(Module module) {
        return enabledMemento.getOrDefault(module, isEnabled(module));
    }

    public void setEnabledMemento(Module module, boolean enabled) {
        enabledMemento.put(module, enabled);
    }

    public long getSeedMemento(Module module) {
        return seedMemento.getOrDefault(module, getSeed(module));
    }

    public void setSeedMemento(Module module, long seed) {
        seedMemento.put(module, seed);
    }

    public void setEnabled(Module module) {
        moduleStates.get(module).enable();
    }

    public void setDisabled(Module module) {
        moduleStates.get(module).disable();
    }

    public void setSeed(Module module, long seed) {
        moduleStates.get(module).setSeed(seed);
    }

    public void setSeedAll(long seed) {
        moduleStates.forEach((module, state) -> state.setSeed(seed));
    }

    public void randomSeed(Module module) {
        moduleStates.get(module).randomSeed();
    }

    public void randomSeedAll() {
        moduleStates.forEach((module, state) -> state.randomSeed());
    }

    public void enableAll() {
        moduleStates.forEach((module, state) -> state.enable());
    }

    public void disableAll() {
        moduleStates.forEach((module, state) -> state.disable());
    }

    public void confirm() {
        moduleStates.forEach((module, state) -> {
            if (getEnabledMemento(module)) {
                state.enable();
            } else {
                state.disable();
            }
            state.setSeed(getSeedMemento(module));
        });
        enabledMemento.clear();
        seedMemento.clear();
    }

    public void cancel() {
        enabledMemento.clear();
        seedMemento.clear();
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        moduleStates.forEach((module, state) -> {
            NbtCompound moduleNbt = new NbtCompound();
            state.writeNbt(moduleNbt);
            nbt.put(module.name(), moduleNbt);
        });
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        moduleStates.forEach((module, state) -> {
            NbtCompound moduleNbt = nbt.getCompound(module.name());
            state.readNbt(moduleNbt);
        });
    }

    public Modules copy() {
        Map<Module, ModuleState> copy = new HashMap<>();
        moduleStates.forEach((module, state) -> copy.put(module, state.copy()));
        return new Modules(copy);
    }

    public List<Module> asList() {
        List<Module> modules = new ArrayList<>();
        forEach(modules::add);
        return modules;
    }

    @NotNull
    @Override
    public Iterator<Module> iterator() {
        return moduleStates.keySet().iterator();
    }
}
