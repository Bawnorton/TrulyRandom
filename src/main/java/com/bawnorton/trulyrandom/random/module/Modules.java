package com.bawnorton.trulyrandom.random.module;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
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
    public static final PacketCodec<RegistryByteBuf, Modules> PACKET_CODEC = PacketCodecs.map(HashMap::new, Module.PACKET_CODEC, ModuleState.PACKET_CODEC)
            .xmap(Modules::new, modules -> new HashMap<>(modules.moduleStates));

    private final Map<Module, ModuleState> moduleStates;
    private final Map<Module, Boolean> enabledMemento = new HashMap<>();
    private final Map<Module, Long> seedMemento = new HashMap<>();

    public Modules() {
        moduleStates = new HashMap<>();
        for (Module module : Module.values()) {
            moduleStates.put(module, module.newModuleState());
        }
    }

    private Modules(Map<Module, ModuleState> moduleStates) {
        if (moduleStates.isEmpty()) {
            moduleStates = new HashMap<>();
            for (Module module : Module.values()) {
                moduleStates.put(module, module.newModuleState());
            }
        }
        this.moduleStates = moduleStates;
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

    public <T extends ModuleState> T getState(Module module, Class<T> stateClass) {
        return stateClass.cast(moduleStates.get(module));
    }

    public Modules copy() {
        Map<Module, ModuleState> copy = new HashMap<>();
        moduleStates.forEach((module, state) -> copy.put(module, state.copy()));
        return new Modules(copy);
    }

    private void copy(@NotNull Modules modules) {
        this.moduleStates.clear();
        this.moduleStates.putAll(modules.moduleStates);
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
