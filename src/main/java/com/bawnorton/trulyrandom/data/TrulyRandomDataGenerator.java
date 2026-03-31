package com.bawnorton.trulyrandom.data;

import com.bawnorton.trulyrandom.data.advancement.TrulyRandomTabAdvancementProvider;
import dev.kikugie.fletching_table.annotation.fabric.Entrypoint;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

@Entrypoint("fabric-datagen")
public class TrulyRandomDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(TrulyRandomTabAdvancementProvider::new);
    }
}
