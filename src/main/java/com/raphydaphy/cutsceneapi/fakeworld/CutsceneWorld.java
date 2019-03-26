package com.raphydaphy.cutsceneapi.fakeworld;

import com.raphydaphy.cutsceneapi.mixin.client.ClientWorldHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
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

	public CutsceneWorld(MinecraftClient client, ClientWorld realWorld)
	{
		super(((ClientWorldHooks) realWorld).getCutsceneNetHandler(), new LevelInfo(realWorld.getLevelProperties()), DimensionType.OVERWORLD, client.getProfiler(), client.worldRenderer);
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

	@Override
	public int getLightLevel(LightType lightType_1, BlockPos blockPos_1)
	{
		if (lightType_1 == LightType.BLOCK)
		{
			return 15;
		}
		return 0;
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
