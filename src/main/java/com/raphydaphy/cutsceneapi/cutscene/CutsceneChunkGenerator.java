package com.raphydaphy.cutsceneapi.cutscene;

import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class CutsceneChunkGenerator extends ChunkGenerator
{
	public CutsceneChunkGenerator(IWorld iWorld_1, BiomeSource biomeSource_1, ChunkGeneratorConfig chunkGeneratorConfig_1)
	{
		super(iWorld_1, biomeSource_1, chunkGeneratorConfig_1);
	}

	@Override
	public void buildSurface(Chunk var1)
	{

	}

	@Override
	public int getSpawnHeight()
	{
		return 0;
	}

	@Override
	public void populateNoise(IWorld var1, Chunk var2)
	{

	}

	@Override
	public int getHeightOnGround(int var1, int var2, Heightmap.Type var3)
	{
		return 0;
	}
}
