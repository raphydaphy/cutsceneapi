package com.raphydaphy.cutsceneapi.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientWorld.class)
public interface ClientWorldHooks
{
	@Accessor("netHandler")
	ClientPlayNetworkHandler getCutsceneNetworkHandler();
}
