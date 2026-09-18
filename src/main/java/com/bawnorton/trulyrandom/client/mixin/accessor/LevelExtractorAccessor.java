//? if >=26.3 {
package com.bawnorton.trulyrandom.client.mixin.accessor;

import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import net.minecraft.client.renderer.extract.LevelExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@MixinEnvironment("client")
@Mixin(LevelExtractor.class)
public interface LevelExtractorAccessor {
    @Invoker("setSectionDirty")
    void trulyrandom$setSectionDirty(int sectionX, int sectionY, int sectionZ, boolean playerChanged);
}
//?}