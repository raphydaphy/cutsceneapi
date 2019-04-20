package com.raphydaphy.cutsceneapi.mixin.client;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererHooks {
    @Invoker("loadShader")
    void useShader(Identifier id);
}
