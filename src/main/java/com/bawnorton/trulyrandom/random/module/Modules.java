package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.state.ModuleState;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Modules implements Iterable<Module> {
    public static final Codec<Modules> CODEC = Codec.unboundedMap(Module.CODEC, ModuleState.CODEC)
            .xmap(Modules::new, modules -> modules.moduleStates);
    public static final StreamCodec<RegistryFriendlyByteBuf, Modules> STREAM_CODEC = ByteBufCodecs.map(HashMap::new, Module.STREAM_CODEC, ModuleState.STREAM_CODEC)
            .map(Modules::new, modules -> new HashMap<>(modules.moduleStates));

    private final Map<Module, ModuleState> moduleStates;
    private final Map<Module, Boolean> enabledMemento = new HashMap<>();
    private final Map<Module, Long> seedMemento = new HashMap<>();

    public Modules() {
        moduleStates = new LinkedHashMap<>();
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
        this.moduleStates = new LinkedHashMap<>();
        for (Module module : Module.values()) {
            this.moduleStates.put(module, moduleStates.getOrDefault(module, module.newModuleState()));
        }
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

    public void newRandomSeed(Module module) {
        moduleStates.get(module).newRandomSeed();
    }

    public void newRandomSeedAll() {
        moduleStates.forEach((_, state) -> state.newRandomSeed());
    }

    public void enableAll() {
        moduleStates.forEach((_, state) -> state.enable());
    }

    public void disableAll() {
        moduleStates.forEach((_, state) -> state.disable());
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
        ModuleState state = moduleStates.get(module);
        if (stateClass.isInstance(state)) {
            return stateClass.cast(state);
        }

        TrulyRandom.LOGGER.error("Tried to parse '{}' module state as '{}', but found '{}'. Was 'type' set correctly? Proceeding with default state settings.", module.name(), stateClass.getSimpleName(), state.getClass().getSimpleName());
        ModuleState expectedState = module.newModuleState();
        expectedState.setSeed(state.getSeed());
        if(state.isEnabled()) {
            expectedState.enable();
        }
        if (state.isVisible()) {
            expectedState.show();
        }
        moduleStates.put(module, expectedState);
        return stateClass.cast(expectedState);
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
