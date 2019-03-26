package com.raphydaphy.cutsceneapi.fakeworld;

import com.raphydaphy.cutsceneapi.mixin.client.ClientWorldHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;

import java.util.HashMap;
import java.util.Map;

public class CutsceneWorld extends ClientWorld
{
	private Map<ChunkPos, CutsceneChunk> chunkMap = new HashMap<>();
	public boolean cloneExisting;

	public CutsceneWorld(MinecraftClient client, ClientWorld realWorld, boolean cloneExisting)
	{
		super(((ClientWorldHooks) realWorld).getCutsceneNetHandler(), new LevelInfo(realWorld.getLevelProperties()), DimensionType.OVERWORLD, client.getProfiler(), client.worldRenderer);
		this.cloneExisting = cloneExisting;
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkY, ChunkStatus status, boolean boolean_1)
	{
		ChunkPos pos = new ChunkPos(chunkX, chunkY);
		if (chunkMap.containsKey(pos)) return chunkMap.get(pos);
		CutsceneChunk chunk = new CutsceneChunk(this, new ChunkPos(chunkX, chunkY), new Biome[]{Biomes.BADLANDS});
		chunkMap.put(pos, chunk);
		return chunk;
	}

	public void addPlayer(ClientPlayerEntity player)
	{
		getPlayers().add(player);
		((ClientWorldHooks)this).getCutsceneEntities().add(player);
	}

	@Override
	public void method_18116()
	{
		super.method_18116();
	}
}
