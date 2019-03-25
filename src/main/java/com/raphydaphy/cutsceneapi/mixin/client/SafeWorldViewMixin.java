package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.SafeWorldView;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(SafeWorldView.class)
public abstract class SafeWorldViewMixin
{
	@Inject(at = @At("RETURN"), method="getBlockState", cancellable = true)
	private void getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> info)
	{
		BlockState state = CutsceneManager.getFakeWorldState(pos, info.getReturnValue());
		if (state != null)
		{
			info.setReturnValue(state);
		}
	}

	@Inject(at = @At("HEAD"), method="getFluidState", cancellable = true)
	private void getFluidState(BlockPos pos, CallbackInfoReturnable<FluidState> info)
	{
		FluidState state = CutsceneManager.getFakeWorldFluid(pos);
		if (state != null)
		{
			info.setReturnValue(state);
		}
	}

	@Inject(at = @At("HEAD"), method="getLightLevel", cancellable = true)
	private void getLightLevel(LightType type, BlockPos pos, CallbackInfoReturnable<Integer> info)
	{
	}
}
