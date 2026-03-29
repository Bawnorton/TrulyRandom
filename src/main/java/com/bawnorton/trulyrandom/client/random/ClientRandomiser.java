package com.bawnorton.trulyrandom.client.random;

import com.bawnorton.trulyrandom.client.random.model.BlockModelRandomiser;
import com.bawnorton.trulyrandom.client.random.model.ItemModelRandomiser;
import com.bawnorton.trulyrandom.client.random.model.ModelRandomiser;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ClientRandomiser extends Randomiser {
    public static final ClientRandomiser DEFAULT = new ClientRandomiser();
    private final BlockModelRandomiser blockModelRandomiser = new BlockModelRandomiser();
    private final ItemModelRandomiser itemModelRandomiser = new ItemModelRandomiser();

    private LootTableTracker lootTableTracker = new LootTableTracker();
    private RecipeTracker recipeTracker = new RecipeTracker();

    private ClientRandomiser(@NotNull Modules modules) {
        super(modules);
    }

    private ClientRandomiser() {
        this(new Modules());
    }

    public LootTableTracker getLootTableTracker() {
        return lootTableTracker;
    }

    public RecipeTracker getRecipeTracker() {
        return recipeTracker;
    }

    public void setLootTableTracker(LootTableTracker tracker) {
        lootTableTracker = tracker;
    }

    public void setRecipeTracker(RecipeTracker tracker) {
        recipeTracker = tracker;
    }

    public void updateBlockModels(Minecraft minecraft, boolean seedChanged) {
        update(blockModelRandomiser, minecraft, modules.isEnabled(blockModelRandomiser.getModule()), seedChanged);
    }

    public void updateItemModels(Minecraft minecraft, boolean seedChanged) {
        update(itemModelRandomiser, minecraft, modules.isEnabled(itemModelRandomiser.getModule()), seedChanged);
    }

    public void updateBlockModels(UnaryMap<BlockState> redirectMap) {
        blockModelRandomiser.updateBlockModels(redirectMap);
        blockModelRandomiser.reloadModels(Minecraft.getInstance());
    }

    public void updateItemModels(UnaryMap<Identifier> redirectMap) {
        itemModelRandomiser.updateItemModels(redirectMap);
        itemModelRandomiser.reloadModels(Minecraft.getInstance());
    }

    private void update(ModelRandomiser randomiser, Minecraft minecraft, boolean randomise, boolean forceRandomise) {
        boolean isRandomised = randomiser.isRandomised();

        if (!randomise && isRandomised) {
            randomiser.reset(minecraft);
            randomiser.setRandomised(false);
        } else if (randomise && (!isRandomised || forceRandomise)) {
            randomiser.randomise(minecraft, modules.getSeed(randomiser.getModule()));
            randomiser.setRandomised(true);
        } else {
            randomiser.reloadModels(minecraft);
        }
    }
}
