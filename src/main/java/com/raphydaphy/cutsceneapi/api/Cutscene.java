package com.raphydaphy.cutsceneapi.api;

import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public interface Cutscene {
    /**
     * @return The cutscene's length in ticks
     */
    int getLength();

    /**
     * @return The ID which the cutscene is stored by in the registry
     */
    Identifier getID();

    /**
     * @param id The ID which the cutscene should be stored by
     */
    void setID(Identifier id);

    /**
     * Called once per tick
     */
    void tick();

    /**
     * @return A copy of the cutscene
     */
    Cutscene copy();

    /**
     * @param initCallback A function to be called before the start of the cutscene
     */
    void setInitCallback(Consumer<Cutscene> initCallback);

    /**
     * @param tickCallback A function to be called once per tick while the cutscene is playing
     */
    void setTickCallback(Consumer<Cutscene> tickCallback);

    /**
     * @param finishCallback A function which will be called at the end of the cutscene
     */
    void setFinishCallback(Consumer<Cutscene> finishCallback);

    /**
     * @return The amount of ticks since the cutscene started
     */
    int getTicks();
}
