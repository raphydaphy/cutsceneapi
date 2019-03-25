package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin
{
	@Inject(at = @At("HEAD"), method = "drawShapeOutline", cancellable = true)
	private static void drawShapeOutline(VoxelShape voxelShape_1, double double_1, double double_2, double double_3, float float_1, float float_2, float float_3, float float_4, CallbackInfo info)
	{
		if (CutsceneManager.hideHud(MinecraftClient.getInstance().player))
		{
			info.cancel();
		}
	}
}
