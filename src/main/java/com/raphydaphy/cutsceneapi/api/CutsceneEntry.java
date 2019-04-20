package com.raphydaphy.cutsceneapi.api;

import net.minecraft.util.Identifier;

public class CutsceneEntry {
    public final Identifier id;
    public final int length;

    public CutsceneEntry(Identifier id, int length) {
        this.id = id;
        this.length = length;
    }
}
