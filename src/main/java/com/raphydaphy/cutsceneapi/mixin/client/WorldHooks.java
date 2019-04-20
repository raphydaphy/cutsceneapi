package com.raphydaphy.cutsceneapi.mixin.client;

import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(World.class)
public interface WorldHooks {
    @Accessor("profiler")
    void setCutsceneProfiler(Profiler profiler);
}
