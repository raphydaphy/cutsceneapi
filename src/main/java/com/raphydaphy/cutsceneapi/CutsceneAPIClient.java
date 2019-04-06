package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.api.ClientCutscene;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneWorldType;
import com.raphydaphy.cutsceneapi.cutscene.DefaultClientCutscene;
import com.raphydaphy.cutsceneapi.cutscene.Path;
import com.raphydaphy.cutsceneapi.cutscene.Transition;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneChunk;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import com.raphydaphy.cutsceneapi.fakeworld.storage.CutsceneChunkSerializer;
import com.raphydaphy.cutsceneapi.network.CutsceneStartPacket;
import com.raphydaphy.cutsceneapi.network.WorldTestPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class CutsceneAPIClient implements ClientModInitializer
{
	public static CutsceneWorld GENERATED;

	public CutsceneAPIClient()
	{
		CutsceneAPI.REALWORLD_CUTSCENE = new DefaultClientCutscene(CutsceneAPI.REALWORLD_CUTSCENE.getLength());
		CutsceneAPI.FAKEWORLD_CUTSCENE_1 = new DefaultClientCutscene(CutsceneAPI.FAKEWORLD_CUTSCENE_1.getLength());
		CutsceneAPI.FAKEWORLD_CUTSCENE_2 = new DefaultClientCutscene(CutsceneAPI.FAKEWORLD_CUTSCENE_2.getLength());
		CutsceneAPI.VOIDWORLD_CUTSCENE = new DefaultClientCutscene(CutsceneAPI.VOIDWORLD_CUTSCENE.getLength());
		CutsceneAPI.GENERATEDWORLD_CUTSCENE = new DefaultClientCutscene(CutsceneAPI.GENERATEDWORLD_CUTSCENE.getLength());
		CutsceneAPI.CACHEDWORLD_CUTSCENE = new DefaultClientCutscene(CutsceneAPI.CACHEDWORLD_CUTSCENE.getLength());
	}

	@Override
	public void onInitializeClient()
	{
		ClientSidePacketRegistry.INSTANCE.register(CutsceneStartPacket.ID, new CutsceneStartPacket.Handler());
		ClientSidePacketRegistry.INSTANCE.register(WorldTestPacket.ID, new WorldTestPacket.Handler());

		ClientCutscene realWorld = (ClientCutscene) CutsceneAPI.REALWORLD_CUTSCENE;
		//realWorld.setIntroTransition(new Transition.DipTo(40, 5, 0, 0, 0));
		//realWorld.setOutroTransition(new Transition.DipTo(40, 5, 0, 0, 0));
		realWorld.setShader(new Identifier("shaders/post/invert.json"));
		realWorld.setInitCallback((cutscene) ->
		{
			MinecraftClient client = MinecraftClient.getInstance();
			ClientCutscene clientCutscene = (ClientCutscene) cutscene;
			float playerX = (float) client.player.x;
			float playerY = (float) client.player.y;
			float playerZ = (float) client.player.z;
			clientCutscene.setCameraPath(Path.builder()
					.with(playerX - 40, playerY + 35, playerZ)
					.with(playerX + 70, playerY + 10, playerZ)
					.build());
			client.player.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 1, 1);
		});

		ClientCutscene fakeWorld2 = (ClientCutscene) CutsceneAPI.FAKEWORLD_CUTSCENE_2;
		fakeWorld2.setIntroTransition(new Transition.FadeFrom(20, 0, 0, 0));
		fakeWorld2.setOutroTransition(new Transition.DipTo(40, 10, 0, 0, 0));
		fakeWorld2.setWorldType(CutsceneWorldType.PREVIOUS);
		fakeWorld2.setInitCallback((cutscene) ->
		{
			MinecraftClient client = MinecraftClient.getInstance();
			ClientCutscene clientCutscene = (ClientCutscene) cutscene;
			float playerX = (float) client.player.x;
			float playerY = (float) client.player.y;
			float playerZ = (float) client.player.z;
			clientCutscene.setCameraPath(Path.builder()
					.with(playerX - 30, playerY + 40, playerZ + 30)
					.with(playerX + 20, playerY + 10, playerZ + -20)
					.build());
		});

		ClientCutscene fakeWorld1 = (ClientCutscene) CutsceneAPI.FAKEWORLD_CUTSCENE_1;
		fakeWorld1.setIntroTransition(new Transition.DipTo(40, 50, 0, 0, 0));
		fakeWorld1.setOutroTransition(new Transition.FadeTo(20, 0, 0, 0));
		fakeWorld1.setWorldType(CutsceneWorldType.CLONE);
		fakeWorld1.setNextCutscene(fakeWorld2);
		fakeWorld1.setInitCallback((cutscene) ->
		{
			MinecraftClient client = MinecraftClient.getInstance();
			ClientCutscene clientCutscene = (ClientCutscene) cutscene;
			float playerX = (float) client.player.x;
			float playerY = (float) client.player.y;
			float playerZ = (float) client.player.z;
			clientCutscene.setCameraPath(Path.builder()
					.with(playerX - 30, playerY + 20, playerZ - 10)
					.with(playerX + 30, playerY + 5, playerZ + 30)
					.build());
			client.player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, 1, 1);
			clientCutscene.getWorld().cutsceneTime = 18000;
		});
		fakeWorld1.setChunkGenCallback((chunk) ->
		{
			int index, x, y, z;
			for (x = 0; x < 16; x++)
			{
				for (y = 0; y < chunk.getWorld().getHeight(); y++)
				{
					for (z = 0; z < 16; z++)
					{
						index = z * 16 * chunk.getHeight() + y * 16 + x;
						BlockState realState = chunk.blockStates[index];
						if (!realState.getFluidState().isEmpty())
						{
							chunk.blockStates[index] = Fluids.LAVA.getDefaultState().getBlockState();
						} else if (!realState.isAir())
						{
							chunk.blockStates[index] = Blocks.NETHERRACK.getDefaultState();
						}
					}
				}
			}
		});

		ClientCutscene voidWorld = (ClientCutscene) CutsceneAPI.VOIDWORLD_CUTSCENE;
		voidWorld.setIntroTransition(new Transition.DipTo(40, 10, 0, 0, 0));
		voidWorld.setOutroTransition(new Transition.DipTo(40, 10, 0, 0, 0));
		voidWorld.setWorldType(CutsceneWorldType.EMPTY);
		voidWorld.setInitCallback((cutscene) ->
		{
			ClientCutscene clientCutscene = (ClientCutscene) cutscene;
			clientCutscene.setCameraPath(Path.builder()
					.with(150, 70, 0)
					.with(40, 50, 0)
					.build());
			CutsceneWorld world = clientCutscene.getWorld();
			Random rand = new Random(world.getSeed());
			for (int x = -10; x < 10; x++)
			{
				for (int y = 30; y <= 40; y++)
				{
					for (int z = -10; z < 10; z++)
					{
						BlockPos pos = new BlockPos(x, y, z);
						if (y == 40)
						{
							if (rand.nextInt(5) == 0)
							{
								Block flower = BlockTags.SMALL_FLOWERS.getRandom(rand);
								world.setBlockState(pos, flower.getDefaultState());
							}
						} else
						{
							world.setBlockState(pos, y == 39 ? Blocks.GRASS_BLOCK.getDefaultState() : Blocks.DIRT.getDefaultState());
						}
					}
				}
			}
		});

		GENERATED = CutsceneWorld.createCached(4783, 15, false, (chunk) -> {});

		ClientCutscene generatedWorld = (ClientCutscene)CutsceneAPI.GENERATEDWORLD_CUTSCENE;
		generatedWorld.setIntroTransition(new Transition.DipTo(20, 50, 1, 1, 1));
		generatedWorld.setOutroTransition(new Transition.FadeTo(20, 1, 1, 1));
		generatedWorld.setWorldType(CutsceneWorldType.CUSTOM);
		generatedWorld.setInitCallback((cutscene) ->
        {
        	MinecraftClient client = MinecraftClient.getInstance();
            ClientCutscene clientCutscene = (ClientCutscene)cutscene;
            GENERATED.setupFrom(client.world);
            clientCutscene.setWorld(GENERATED);
	        clientCutscene.setCameraPath(new Path.Builder().with(-200, 90, 0).with(200, 100, 30).build());
        });

		ClientCutscene cachedWorld = (ClientCutscene)CutsceneAPI.CACHEDWORLD_CUTSCENE;
		cachedWorld.setIntroTransition(new Transition.DipTo(20, 50, 1, 1, 1));
		cachedWorld.setOutroTransition(new Transition.FadeTo(20, 1, 1, 1));
		cachedWorld.setWorldType(CutsceneWorldType.CUSTOM);
		cachedWorld.setInitCallback((cutscene) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			ClientCutscene clientCutscene = (ClientCutscene)cutscene;
			clientCutscene.setCameraPath(new Path.Builder().with(-200, 90, 0).with(200, 100, 30).build());
			CutsceneWorld cutsceneWorld = new CutsceneWorld(client, client.world, null, false);
			int radius = 15;
			for (int chunkX = - radius; chunkX <= radius; chunkX++)
			{
				for (int chunkZ = -radius; chunkZ <= radius; chunkZ++)
				{
					File file = new File("cutscene_chunks.mca");
					if (file.exists())
					{
						ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
						CompoundTag chunkData;
						try
						{
							chunkData = CutsceneChunkSerializer.getTagFromFile(file, chunkPos, false);
						} catch (IOException e)
						{
							CutsceneAPI.getLogger().error("Failed to deserialize cutscene chunk! Printing stack trace...");
							e.printStackTrace();
							continue;
						}
						Chunk chunk = CutsceneChunkSerializer.deserialize(cutsceneWorld, chunkPos, chunkData);
						BlockState[] blockStates = new BlockState[16 * cutsceneWorld.getHeight() * 16];
						int x, y, z, index;
						for (x = 0; x < 16; x++)
						{
							for (y = 0; y < cutsceneWorld.getHeight(); y++)
							{
								for (z = 0; z < 16; z++)
								{
									index = z * 16 * cutsceneWorld.getHeight() + y * 16 + x;
									blockStates[index] = chunk.getBlockState(new BlockPos(chunk.getPos().x + x, y, chunk.getPos().z + z));
								}
							}
						}
						CutsceneChunk cutsceneChunk = new CutsceneChunk(cutsceneWorld, chunkPos, chunk.getBiomeArray(), blockStates);
						cutsceneWorld.putChunk(cutsceneChunk);
					}
				}
			}
			clientCutscene.setWorld(cutsceneWorld);
		});

	}
}
