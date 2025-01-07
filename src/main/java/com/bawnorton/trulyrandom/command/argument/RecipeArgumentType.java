package com.bawnorton.trulyrandom.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;

public class RecipeArgumentType extends RegistryEntryArgumentType<Recipe<?>> {
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        ReloadableRegistries.Lookup lookup = context.getSource().getServer().getReloadableRegistries();
        return CommandSource.suggestIdentifiers(lookup.getIds(RegistryKeys.RECIPE), builder);
    };

    public RecipeArgumentType(CommandRegistryAccess registryAccess) {
        super(registryAccess, RegistryKeys.RECIPE, RegistryElementCodec.of(RegistryKeys.RECIPE, Recipe.CODEC));
    }

    public static RegistryEntry<Recipe<?>> getRecipe(CommandContext<ServerCommandSource> context, String argument) {
        return context.getArgument(argument, RegistryEntry.class);
    }
}
