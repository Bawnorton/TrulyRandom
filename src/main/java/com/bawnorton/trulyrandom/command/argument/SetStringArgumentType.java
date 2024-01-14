package com.bawnorton.trulyrandom.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class SetStringArgumentType implements ArgumentType<String> {
    final List<String> options;

    SetStringArgumentType(List<String> options) {
        this.options = options;
    }

    public static SetStringArgumentType of(String... options) {
        return new SetStringArgumentType(Stream.of(options).map(String::toLowerCase).map(String::trim).toList());
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readString().toLowerCase();
        if (options.contains(string)) {
            return string;
        } else {
            throw new SimpleCommandExceptionType(Text.literal("Invalid selection")).createWithContext(reader);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (!(context.getSource() instanceof CommandSource)) {
            return Suggestions.empty();
        }

        StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        String input;
        try {
            input = reader.readString();
        } catch (CommandSyntaxException ignored) {
            return Suggestions.empty();
        }
        String lowercaseInput = input.toLowerCase();
        options.stream()
                .filter(option -> option.startsWith(lowercaseInput))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }
}
