package com.bawnorton.trulyrandom.random;

import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public abstract class Randomiser {
    protected @NotNull Modules modules;

    protected Randomiser(@NotNull Modules modules) {
        this.modules = modules;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("modules",  modules.writeNbt(new NbtCompound()));
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        modules.readNbt(nbt.getCompound("modules"));
    }

    public @NotNull Modules getModules() {
        return modules;
    }

    public void setModules(@NotNull Modules modules) {
        this.modules = modules;
    }

    public Modules getCopiedModules() {
        return modules.copy();
    }
}
