package com.raphydaphy.cutsceneapi.fakeworld;

import com.raphydaphy.cutsceneapi.mixin.client.ClientWorldHooks;
import com.raphydaphy.cutsceneapi.mixin.client.WorldHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public class CutsceneWorld extends ClientWorld
{
	public ClientWorld realWorld;
	public boolean cloneExisting = false;
	public long cutsceneTime;
	private Map<ChunkPos, CutsceneChunk> chunkMap = new HashMap<>();
	private CutsceneChunkManager cutsceneChunkManager;

	public CutsceneWorld(MinecraftClient client, ClientWorld realWorld, boolean cloneExisting)
	{
		this(((ClientWorldHooks) realWorld).getCutsceneNetHandler(), new LevelInfo(realWorld.getLevelProperties()), DimensionType.OVERWORLD, 1, client.getProfiler(), client.worldRenderer);
		this.realWorld = realWorld;
		this.cloneExisting = cloneExisting;
	}

	public CutsceneWorld(ClientPlayNetworkHandler netHandler, LevelInfo levelInfo, DimensionType dimension, int id, Profiler profiler, WorldRenderer renderer)
	{
		super(netHandler, levelInfo, dimension, id, profiler, renderer);
		cutsceneChunkManager = new CutsceneChunkManager(this);
	}

	public void setupFrom(ClientWorld other)
	{
		((ClientWorldHooks)this).setCutsceneNetHandler(((ClientWorldHooks)other).getCutsceneNetHandler());
		((ClientWorldHooks)this).setWorldRenderer(MinecraftClient.getInstance().worldRenderer);
		((WorldHooks)this).setCutsceneProfiler(other.getProfiler());
		this.realWorld = other;
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus status, boolean boolean_1)
	{
		ChunkPos pos = new ChunkPos(chunkX, chunkZ);
		if (chunkMap.containsKey(pos)) return chunkMap.get(pos);
		Biome[] biomes = new Biome[16 * 16];
		if (cloneExisting && realWorld != null)
		{
			for (int x = 0; x < 16; x++)
			{
				for (int z = 0; z < 16; z++)
				{
					biomes[x * 16 + z] = realWorld.getBiome(new BlockPos(chunkX * 16 + x, 0, chunkZ * 16 + z));
				}
			}
		} else
		{
			Arrays.fill(biomes, Biomes.PLAINS);
		}
		CutsceneChunk chunk = new CutsceneChunk(this, new ChunkPos(chunkX, chunkZ), biomes);
		chunkMap.put(pos, chunk);
		return chunk;
	}

	public void putChunk(CutsceneChunk chunk)
	{
		chunkMap.put(chunk.getPos(), chunk);
	}

	public void addPlayer(ClientPlayerEntity player)
	{
		getPlayers().add(player);
		((ClientWorldHooks) this).getCutsceneEntities().add(player);
	}

	@Override
	public void method_18116()
	{
		super.method_18116();
	}

	@Override
	public ClientChunkManager method_2935()
	{
		return cutsceneChunkManager;
	}

	@Override
	public ChunkManager getChunkManager()
	{
		return cutsceneChunkManager;
	}

	@Override
	public void method_8441(BooleanSupplier booleanSupplier_1)
	{
		this.getWorldBorder().tick();
		this.tickTime();
		this.getProfiler().push("blocks");
		this.cutsceneChunkManager.tick(booleanSupplier_1);
		((ClientWorldHooks) this).updateCutsceneLighting();
		this.getProfiler().pop();
		cutsceneTime++;
	}

	@Override
	public float getMoonSize()
	{
		return Dimension.MOON_PHASE_TO_SIZE[this.getDimension().getMoonPhase(getTimeOfDay())];
	}

	public int getMoonPhase()
	{
		return this.getDimension().getMoonPhase(getTimeOfDay());
	}

	@Override
	public float getSkyAngle(float float_1)
	{
		return this.getDimension().getSkyAngle(getTimeOfDay(), float_1);
	}

	@Override
	public long getTimeOfDay()
	{
		return cutsceneTime;
	}
}
