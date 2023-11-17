package com.bawnorton.trulyrandom.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Modules implements Iterable<Module> {
    public static final Codec<Modules> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Module.CODEC.listOf().fieldOf("modules").forGetter(Modules::getModules)
    ).apply(instance, Modules::new));

    private final List<Module> modules;
    private final Map<Module, Boolean> memento = new HashMap<>();

    private Modules(List<Module> modules) {
        this.modules = modules;
    }

    public Modules() {
        this.modules = List.of(Module.values());
    }

    public List<Module> getModules() {
        return modules;
    }

    public static Modules fromPacket(PacketByteBuf buf) {
        Modules modules = new Modules();
        modules.modules.forEach(module -> module.setEnabled(buf.readBoolean()));
        return modules;
    }

    public void write(PacketByteBuf buf) {
        modules.forEach(module -> buf.writeBoolean(module.isEnabled()));
    }

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

    public void writeNbt(NbtCompound nbt) {
        modules.forEach(module -> module.writeNbt(nbt));
    }

    public void readNbt(NbtCompound nbt) {
        modules.forEach(module -> module.readNbt(nbt));
    }

    @NotNull
    @Override
    public Iterator<Module> iterator() {
        return modules.iterator();
    }
}
