package com.raphydaphy.cutsceneapi.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftClient.class)
public interface MinecraftClientHooks {
    @Invoker("setWorld")
    void setCutsceneWorld(ClientWorld world);
}
