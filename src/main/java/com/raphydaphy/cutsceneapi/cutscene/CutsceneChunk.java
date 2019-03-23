package com.raphydaphy.cutsceneapi.cutscene;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.Heightmap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class CutsceneChunk implements Chunk
{
	@Nullable
	@Override
	public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3)
	{
		return null;
	}

	@Override
	public void setBlockEntity(BlockPos var1, BlockEntity var2)
	{

	}

	@Override
	public void addEntity(Entity var1)
	{

	}

	@Override
	public Set<BlockPos> getBlockEntityPositions()
	{
		return null;
	}

	@Override
	public ChunkSection[] getSectionArray()
	{
		return new ChunkSection[0];
	}

	@Nullable
	@Override
	public LightingProvider getLightingProvider()
	{
		return null;
	}

	@Override
	public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps()
	{
		return null;
	}

	@Override
	public void setHeightmap(Heightmap.Type var1, long[] var2)
	{

	}

	@Override
	public Heightmap getHeightmap(Heightmap.Type var1)
	{
		return null;
	}

	@Override
	public int sampleHeightmap(Heightmap.Type var1, int var2, int var3)
	{
		return 0;
	}

	@Override
	public ChunkPos getPos()
	{
		return null;
	}

	@Override
	public void setLastSaveTime(long var1)
	{

	}

	@Override
	public Map<String, StructureStart> getStructureStarts()
	{
		return null;
	}

	@Override
	public void setStructureStarts(Map<String, StructureStart> var1)
	{

	}

	@Override
	public Biome[] getBiomeArray()
	{
		return new Biome[0];
	}

	@Override
	public void setShouldSave(boolean var1)
	{

	}

	@Override
	public boolean needsSaving()
	{
		return false;
	}

	@Override
	public ChunkStatus getStatus()
	{
		return null;
	}

	@Override
	public void removeBlockEntity(BlockPos var1)
	{

	}

	@Override
	public void setLightingProvider(LightingProvider var1)
	{

	}

	@Override
	public ShortList[] getPostProcessingLists()
	{
		return new ShortList[0];
	}

	@Override
	public Stream<BlockPos> getLightSourcesStream()
	{
		return null;
	}

	@Override
	public TickScheduler<Block> getBlockTickScheduler()
	{
		return null;
	}

	@Override
	public TickScheduler<Fluid> getFluidTickScheduler()
	{
		return null;
	}

	@Override
	public UpgradeData getUpgradeData()
	{
		return null;
	}

	@Override
	public void setInhabitedTime(long var1)
	{

	}

	@Override
	public long getInhabitedTime()
	{
		return 0;
	}

	@Override
	public boolean isLightOn()
	{
		return false;
	}

	@Override
	public void setLightOn(boolean var1)
	{

	}

	@Nullable
	@Override
	public StructureStart getStructureStart(String var1)
	{
		return null;
	}

	@Override
	public void setStructureStart(String var1, StructureStart var2)
	{

	}

	@Override
	public LongSet getStructureReferences(String var1)
	{
		return null;
	}

	@Override
	public void addStructureReference(String var1, long var2)
	{

	}

	@Override
	public Map<String, LongSet> getStructureReferences()
	{
		return null;
	}

	@Override
	public void setStructureReferences(Map<String, LongSet> var1)
	{

	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos var1)
	{
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos var1)
	{
		return null;
	}

	@Override
	public FluidState getFluidState(BlockPos var1)
	{
		return null;
	}
}
