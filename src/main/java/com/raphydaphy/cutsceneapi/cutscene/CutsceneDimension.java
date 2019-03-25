package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.CavesChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;

public class CutsceneDimension extends Dimension
{
	public CutsceneDimension(World world_1, DimensionType dimensionType_1)
	{
		super(world_1, dimensionType_1);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator()
	{
		CavesChunkGeneratorConfig cavesChunkGeneratorConfig_1 = ChunkGeneratorType.CAVES.createSettings();
		cavesChunkGeneratorConfig_1.setDefaultBlock(Blocks.GRASS.getDefaultState());
		cavesChunkGeneratorConfig_1.setDefaultFluid(Blocks.WATER.getDefaultState());
		return ChunkGeneratorType.CAVES.create(this.world, BiomeSourceType.FIXED.applyConfig((BiomeSourceType.FIXED.getConfig()).setBiome(Biomes.JUNGLE)), cavesChunkGeneratorConfig_1);
	}

	@Override
	public BlockPos getSpawningBlockInChunk(ChunkPos var1, boolean var2)
	{
		return null;
	}

	@Override
	public BlockPos getTopSpawningBlockPosition(int var1, int var2, boolean var3)
	{
		return null;
	}

	@Override
	public float getSkyAngle(long var1, float var3)
	{
		return 0;
	}

	@Override
	public boolean hasVisibleSky()
	{
		return false;
	}

	@Override
	public Vec3d getFogColor(float var1, float var2)
	{
		return new Vec3d(0.20000000298023224D, 0.029999999329447746D, 0.029999999329447746D);
	}

	@Override
	public boolean canPlayersSleep()
	{
		return false;
	}

	@Override
	public boolean shouldRenderFog(int var1, int var2)
	{
		return false;
	}

	@Override
	public DimensionType getType()
	{
		return CutsceneAPI.CUTSCENE_DIMENSION;
	}
}
