package com.raphydaphy.cutsceneapi.cutscene;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;

import java.util.Random;

public class CutsceneWorld extends ClientWorld
{
	public CutsceneWorld(ClientPlayNetworkHandler handler, LevelInfo info, Profiler profiler_1, WorldRenderer worldRenderer_1)
	{
		super(handler, info, DimensionType.THE_END, profiler_1, worldRenderer_1);
	}

	@Override
	public BlockState getBlockState(BlockPos pos)
	{
		return Blocks.ENCHANTING_TABLE.getDefaultState();
	}

	@Override
	public FluidState getFluidState(BlockPos var1)
	{
		return Fluids.WATER.getState(false);
	}
}
