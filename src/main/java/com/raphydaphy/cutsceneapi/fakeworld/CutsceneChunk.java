package com.raphydaphy.cutsceneapi.fakeworld;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

import java.util.Arrays;

public class CutsceneChunk extends WorldChunk
{
	private BlockState[] blockStates;

	public CutsceneChunk(World world, ChunkPos pos, Biome[] biomes)
	{
		super(world, pos, biomes);
		blockStates = new BlockState[16 * world.getHeight() * 16];
		Arrays.fill(blockStates, Blocks.AIR.getDefaultState());
	}

	@Override
	public boolean method_12228(int minY, int maxY) // Are all of the subchunks from minY to maxY empty?
	{
		if (minY < 0)
		{
			minY = 0;
		}

		if (maxY >= 256)
		{
			maxY = 255;
		}

		for (int y = minY; y <= maxY; y += 16)
		{
			if (!isSubChunkEmpty(y))
			{
				return false;
			}
		}

		return true;
	}

	private boolean isSubChunkEmpty(int gridY)
	{
		for (int y = gridY; y < gridY + 16; y++)
		{
			for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					int index = z * 16 * this.getHeight() + y * 16 + x;
					if (!blockStates[index].isAir())
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	private int getIndex(BlockPos pos)
	{
		return getIndex(pos.getX(), pos.getY(), pos.getZ());
	}

	private int getIndex(int posX, int posY, int posZ)
	{
		int x = posX - this.getPos().getStartX();
		int z = posZ - this.getPos().getStartZ();
		return z * 16 * this.getHeight() + posY * 16 + x;
	}

	@Override
	public Biome getBiome(BlockPos blockPos_1)
	{
		return getBiomeArray()[0];
	}

	@Override
	public BlockState getBlockState(BlockPos pos)
	{
		if (pos.getY() >= 0)
		{
			int index = getIndex(pos);
			if (index < blockStates.length && index >= 0)
			{
				return blockStates[index];
			}
			CutsceneAPI.getLogger().warn("Tried to get BlockState out of chunk with world position " + pos.toString() + " and index " + index);
		}
		return Blocks.VOID_AIR.getDefaultState();
	}

	@Override
	public BlockState setBlockState(BlockPos pos, BlockState state, boolean boolean_1)
	{
		if (pos.getY() >= 0 && pos.getY() <= getWorld().getHeight())
		{
			int index = getIndex(pos);
			if (index < blockStates.length)
			{
				blockStates[index] = state;
				return state;
			}
			CutsceneAPI.getLogger().warn("Tried to set BlockState out of chunk with position " + pos.toString());
		}
		return Blocks.VOID_AIR.getDefaultState();
	}

	@Override
	public FluidState getFluidState(int x, int y, int z)
	{
		return Fluids.EMPTY.getDefaultState();
	}
}
