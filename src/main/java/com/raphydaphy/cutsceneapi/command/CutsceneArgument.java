package com.raphydaphy.cutsceneapi.command;

import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public class CutsceneArgument implements Predicate<Identifier> {
    private final Identifier id;

    public CutsceneArgument(Identifier id) {
        this.id = id;
    }

    public Identifier getID() {
        return id;
    }

    @Override
    public boolean test(Identifier identifier) {
        return id.equals(identifier);
    }
}
