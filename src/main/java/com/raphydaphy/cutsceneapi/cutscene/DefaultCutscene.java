package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.api.Cutscene;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class DefaultCutscene implements Cutscene {
    // Settings
    protected int length;
    protected Consumer<Cutscene> initCallback;
    protected Consumer<Cutscene> tickCallback;
    protected Consumer<Cutscene> finishCallback;
    // Data
    protected int ticks = 0;
    protected boolean started = false;
    protected boolean ended = false;
    private Identifier id;

    public DefaultCutscene(int length) {
        this.length = length;
    }

    @Override
    public void tick() {
        if (ticks < length) {
            if (ticks == 0) {
                if (this.initCallback != null) this.initCallback.accept(this);
                started = true;
            }

            // Callback
            if (tickCallback != null) tickCallback.accept(this);

            ticks++;

            if (ticks == length) {
                if (finishCallback != null) finishCallback.accept(this);
            }
        }
    }

    @Override
    public void setInitCallback(Consumer<Cutscene> initCallback) {
        this.initCallback = initCallback;
    }

    @Override
    public void setTickCallback(Consumer<Cutscene> tickCallback) {
        this.tickCallback = tickCallback;
    }

    @Override
    public void setFinishCallback(Consumer<Cutscene> finishCallback) {
        this.finishCallback = finishCallback;
    }

    @Override
    public Cutscene copy() {
        DefaultCutscene cutscene = new DefaultCutscene(length);
        cutscene.initCallback = this.initCallback;
        cutscene.tickCallback = this.tickCallback;
        cutscene.finishCallback = this.finishCallback;
        cutscene.id = id;
        return cutscene;
    }

    @Override
    public int getTicks() {
        return ticks;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public Identifier getID() {
        return id;
    }

    @Override
    public void setID(Identifier id) {
        this.id = id;
    }
}
