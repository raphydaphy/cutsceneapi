package com.raphydaphy.cutsceneapi.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ClientWorld.class)
public interface ClientWorldHooks
{
	@Accessor("netHandler")
	ClientPlayNetworkHandler getCutsceneNetHandler();

	@Accessor("globalEntities")
	List<Entity> getCutsceneEntities();
}
