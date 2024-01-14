package com.bawnorton.trulyrandom.random.module;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public class Modules implements Iterable<Module> {
    public static final Codec<Modules> CODEC = Codec.unboundedMap(Module.CODEC, ModuleState.CODEC)
            .xmap(Modules::new, Modules::getModules);

    private final Map<Module, ModuleState> modules;
    private final Map<Module, Boolean> enabledMemento = new HashMap<>();
    private final Map<Module, Long> seedMemento = new HashMap<>();

    public Modules() {
        modules = new HashMap<>();
        for (Module module : Module.values()) {
            modules.put(module, new ModuleState());
        }
    }

    private Modules(Map<Module, ModuleState> modules) {
        this.modules = modules;
    }

    public static Modules fromPacket(PacketByteBuf buf) {
        Modules modules = new Modules();
        modules.read(buf);
        return modules;
    }

    public static Modules fromNbt(NbtCompound nbt) {
        Modules modules = new Modules();
        modules.readNbt(nbt);
        return modules;
    }

    public Map<Module, ModuleState> getModules() {
        return modules;
    }

    public void write(PacketByteBuf buf) {
        buf.writeVarInt(modules.size());
        modules.forEach((module, state) -> {
            buf.writeString(module.name());
            state.write(buf);
        });
    }

    public void read(PacketByteBuf buf) {
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            Module module = Module.valueOf(buf.readString());
            ModuleState state = modules.get(module);
            state.read(buf);
        }
    }

    public boolean isDisabled(Module module) {
        return !isEnabled(module);
    }

    public boolean isEnabled(Module module) {
        return modules.get(module).isEnabled();
    }

    public long getSeed(Module module) {
        return modules.get(module).getSeed();
    }

    public void hideServerSide() {
        modules.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isServerSide())
                .forEach(entry -> entry.getValue().hide());
    }

    public void showServerSide() {
        modules.entrySet()
                .stream()
                .filter(entry -> entry.getKey().isServerSide())
                .forEach(entry -> entry.getValue().show());
    }

    public boolean getMemento(Module module) {
        return enabledMemento.getOrDefault(module, isEnabled(module));
    }

    public void setMemento(Module module, boolean enabled) {
        enabledMemento.put(module, enabled);
    }

    public long getSeedMemento(Module module) {
        return seedMemento.getOrDefault(module, getSeed(module));
    }

    public void setSeedMemento(Module module, long seed) {
        seedMemento.put(module, seed);
    }

    public void setEnabled(Module module) {
        modules.get(module).enable();
    }

    public void setDisabled(Module module) {
        modules.get(module).disable();
    }

    public void setSeed(Module module, long seed) {
        modules.get(module).setSeed(seed);
    }

    public void setSeedAll(long seed) {
        modules.forEach((module, state) -> state.setSeed(seed));
    }

    public void randomSeed(Module module) {
        modules.get(module).randomSeed();
    }

    public void randomSeedAll() {
        modules.forEach((module, state) -> state.randomSeed());
    }

    public void enableAll() {
        modules.forEach((module, state) -> state.enable());
    }

    public void disableAll() {
        modules.forEach((module, state) -> state.disable());
    }

    public void confirm() {
        modules.forEach((module, state) -> {
            if (getMemento(module)) {
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
        modules.forEach((module, state) -> {
            NbtCompound moduleNbt = new NbtCompound();
            state.writeNbt(moduleNbt);
            nbt.put(module.name(), moduleNbt);
        });
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        modules.forEach((module, state) -> {
            NbtCompound moduleNbt = nbt.getCompound(module.name());
            state.readNbt(moduleNbt);
        });
    }

    public Modules copy() {
        Map<Module, ModuleState> copy = new HashMap<>();
        modules.forEach((module, state) -> copy.put(module, state.copy()));
        return new Modules(copy);
    }

    public void forEachVisible(Consumer<Module> consumer) {
        modules.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().ordinal()))
                .filter(entry -> entry.getValue().isVisible())
                .forEach(entry -> consumer.accept(entry.getKey()));
    }

    @NotNull
    @Override
    public Iterator<Module> iterator() {
        return modules.keySet().iterator();
    }
}
