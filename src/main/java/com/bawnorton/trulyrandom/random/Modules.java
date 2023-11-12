package com.bawnorton.trulyrandom.random;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Modules implements Iterable<Module> {
    private final List<Module> modules = List.of(Module.values());
    private final Map<Module, Boolean> memento = new HashMap<>();

    public boolean isDisabled(Module module) {
        return !module.isEnabled();
    }

    public boolean isEnabled(Module module) {
        return module.isEnabled();
    }

    public boolean getMemento(Module module) {
        return memento.getOrDefault(module, module.isEnabled());
    }

    public void setMemento(Module module, Boolean enabled) {
        memento.put(module, enabled);
    }

    public void confirm() {
        modules.forEach(module -> module.setEnabled(getMemento(module)));
        memento.clear();
    }

    public void cancel() {
        memento.clear();
    }

    @NotNull
    @Override
    public Iterator<Module> iterator() {
        return modules.iterator();
    }
}
