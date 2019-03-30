package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.api.ClientCutscene;
import com.raphydaphy.cutsceneapi.cutscene.*;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import com.raphydaphy.cutsceneapi.network.CutsceneStartPacket;
import com.raphydaphy.cutsceneapi.network.WorldTestPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class CutsceneAPIClient implements ClientModInitializer
{
	public CutsceneAPIClient()
	{
		CutsceneAPI.REALWORLD_CUTSCENE = new DefaultClientCutscene(CutsceneAPI.REALWORLD_CUTSCENE.getLength());
		CutsceneAPI.FAKEWORLD_CUTSCENE_1 = new DefaultClientCutscene(CutsceneAPI.FAKEWORLD_CUTSCENE_1.getLength());
		CutsceneAPI.FAKEWORLD_CUTSCENE_2 = new DefaultClientCutscene(CutsceneAPI.FAKEWORLD_CUTSCENE_2.getLength());
		CutsceneAPI.VOIDWORLD_CUTSCENE = new DefaultClientCutscene(CutsceneAPI.VOIDWORLD_CUTSCENE.getLength());
	}

	@Override
	public void onInitializeClient()
	{
		ClientSidePacketRegistry.INSTANCE.register(CutsceneStartPacket.ID, new CutsceneStartPacket.Handler());
		ClientSidePacketRegistry.INSTANCE.register(WorldTestPacket.ID, new WorldTestPacket.Handler());

		ClientCutscene realworld = (ClientCutscene)CutsceneAPI.REALWORLD_CUTSCENE;
		realworld.setIntroTransition(new Transition.DipTo(40, 5, 0, 0, 0));
		realworld.setOutroTransition(new Transition.DipTo(40, 5, 0, 0, 0));
		realworld.setInitCallback((cutscene) ->
		{
			MinecraftClient client = MinecraftClient.getInstance();
			ClientCutscene clientCutscene = (ClientCutscene)cutscene;
			float playerX = (float) client.player.x;
			float playerY = (float) client.player.y;
			float playerZ = (float) client.player.z;
			clientCutscene.setCameraPath(new Path()
					.withPoint(playerX - 40, playerY + 35, playerZ).withPoint(playerX + 70, playerY + 10, playerZ));
			client.player.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 1, 1);
		});

		ClientCutscene fakeworld_1 = (ClientCutscene)CutsceneAPI.FAKEWORLD_CUTSCENE_1;
		fakeworld_1.setIntroTransition(new Transition.DipTo(40, 50, 0, 0, 0));
		fakeworld_1.setOutroTransition(new Transition.DipTo(40, 10, 0, 0, 0));
		fakeworld_1.setWorldType(CutsceneWorldType.CLONE);
		fakeworld_1.setInitCallback((cutscene) ->
		{
			MinecraftClient client = MinecraftClient.getInstance();
			ClientCutscene clientCutscene = (ClientCutscene)cutscene;
			float playerX = (float) client.player.x;
			float playerY = (float) client.player.y;
			float playerZ = (float) client.player.z;
			clientCutscene.setCameraPath(new Path()
					.withPoint(playerX - 30, playerY + 20, playerZ - 10).withPoint(playerX + 30, playerY + 5, playerZ + 30));
			client.player.playSound(SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, 1, 1);
			clientCutscene.getWorld().cutsceneTime = 18000;

			System.out.println(clientCutscene.getWorld().getTimeOfDay());
		});
		fakeworld_1.setChunkGenCallback((chunk) ->
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

		ClientCutscene voidworld = (ClientCutscene)CutsceneAPI.VOIDWORLD_CUTSCENE;
		voidworld.setIntroTransition(new Transition.DipTo(40, 10, 0, 0, 0));
		voidworld.setOutroTransition(new Transition.DipTo(40, 10, 0, 0, 0));
		voidworld.setWorldType(CutsceneWorldType.EMPTY);
		voidworld.setInitCallback((cutscene) ->
		{
			BlockPos player = MinecraftClient.getInstance().player.getBlockPos();
			ClientCutscene clientCutscene = (ClientCutscene)cutscene;
			clientCutscene.setCameraPath(new Path().withPoint(player.getX() - 150, 70, player.getZ()).withPoint(player.getX() + 40, 50, player.getZ()));
			CutsceneWorld world = clientCutscene.getWorld();
			Random rand = new Random(world.getSeed());
			for (int x = -10; x < 10; x++)
			{
				for (int y = 30; y <= 40; y++)
				{
					for (int z = -10; z < 10; z++)
					{
						BlockPos pos = new BlockPos(player.getX() + x, y, player.getZ() + z);
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
	}
}
