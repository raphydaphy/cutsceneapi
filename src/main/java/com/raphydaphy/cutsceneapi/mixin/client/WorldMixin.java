package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin
{
	@Shadow
	@Final
	public boolean isClient;

	@Inject(at = @At("RETURN"), method = "getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", cancellable = true)
	private void getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> info)
	{
		if (this.isClient)
		{
			BlockState state = CutsceneManager.getFakeWorldState(pos, info.getReturnValue());
			if (state != null)
			{
				info.setReturnValue(state);
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "getFluidState", cancellable = true)
	private void getFluidState(BlockPos pos, CallbackInfoReturnable<FluidState> info)
	{
		if (this.isClient)
		{
			FluidState state = CutsceneManager.getFakeWorldFluid(pos);
			if (state != null)
			{
				info.setReturnValue(state);
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "getLightLevel(Lnet/minecraft/world/LightType;Lnet/minecraft/util/math/BlockPos;)I", cancellable = true)
	private void getLightLevel(LightType type, BlockPos pos, CallbackInfoReturnable<Integer> info)
	{
		if (this.isClient)
		{
			int light = CutsceneManager.getFakeWorldLight(type, pos);
			if (light != -1)
			{
				info.setReturnValue(light);
			}
		}
	}
}
