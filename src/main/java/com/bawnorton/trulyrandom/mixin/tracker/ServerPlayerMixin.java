package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.ResultHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

import static com.bawnorton.trulyrandom.TrulyRandom.getServer;
import static com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker.LAST_RECIPE_OUTPUT;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerMixin extends PlayerMixin {
    protected ServerPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "triggerRecipeCrafted",
            at = @At("HEAD")
    )
    private void trackRecipe(RecipeHolder<?> recipe, List<ItemStack> ingredients, CallbackInfo ci) {
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
