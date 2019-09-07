package com.raphydaphy.cutsceneapi.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneRegistry;
import net.minecraft.server.command.CommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class CutsceneArgumentType implements ArgumentType<CutsceneArgument> {
    private static final Collection<String> EXAMPLES = Collections.singletonList("cutsceneapi:demo");

    public static CutsceneArgumentType create() {
        return new CutsceneArgumentType();
    }

    public static CutsceneArgument get(final CommandContext<?> context, final String name) {
        return context.getArgument(name, CutsceneArgument.class);
    }

    @Override
    public CutsceneArgument parse(StringReader reader) throws CommandSyntaxException {
        Identifier id = Identifier.fromCommandInput(reader);
        if (!CutsceneRegistry.getIDs().contains(id))
            throw new CommandSyntaxException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(), new TranslatableText("arguments.cutsceneapi.invalid"));
        return new CutsceneArgument(id);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(CutsceneRegistry.getIDs(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
