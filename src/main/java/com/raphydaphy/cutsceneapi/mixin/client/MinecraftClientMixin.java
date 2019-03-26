package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{
	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	public ClientWorld world;

	@Inject(at = @At(value = "INVOKE_STRING", args = "ldc=gameRenderer", target = "Lnet/minecraft/util/profiler/DisableableProfiler;swap(Ljava/lang/String;)V"), method = "render")
	private void worldRenderTick(boolean renderWorldIn, CallbackInfo info)
	{
		if (world != null)
		{
			if (CutsceneManager.isActive(player))
			{
				CutsceneManager.updateLook();
			}
		}
	}
}
