package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
	@Shadow private ClientWorld world;

	@Inject(at = @At("HEAD"), method="onBlockUpdate", cancellable = true)
	private void onBlockUpdate(BlockUpdateS2CPacket blockUpdateS2CPacket_1, CallbackInfo info)
	{
		if (this.world instanceof CutsceneWorld)
		{
			ClientWorld realWorld = CutsceneManager.getRealWorld();
			if (realWorld != null)
			{
				realWorld.method_2937(blockUpdateS2CPacket_1.getPos(), blockUpdateS2CPacket_1.getState());
			}
			info.cancel();
		}
	}
}
