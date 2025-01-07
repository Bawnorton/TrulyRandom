package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.ResultHolder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import static com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker.LAST_RECIPE_OUTPUT;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntityMixin {
    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "onRecipeCrafted",
            at = @At("HEAD")
    )
    private void trackRecipe(RecipeEntry<?> recipe, List<ItemStack> ingredients, CallbackInfo ci) {
        ItemStack result = LAST_RECIPE_OUTPUT.get();
        if(result.isEmpty()) {
            if(recipe.value() instanceof ResultHolder resultHolder) {
                result = resultHolder.trulyrandom$getResult();
            }
            if(result.isEmpty()) {
                throw new IllegalStateException("Recipe output is empty");
            }
        }
        TrulyRandom.getRandomiser(getServer())
                .getRecipeRandomiser()
                .trackRecipeOutput(trulyrandom$getTeam(), recipe.id(), result.copy());
    }
}
