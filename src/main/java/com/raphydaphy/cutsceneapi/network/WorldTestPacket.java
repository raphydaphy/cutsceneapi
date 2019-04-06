package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.crochet.network.IPacket;
import com.raphydaphy.crochet.network.MessageHandler;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.fakeworld.storage.CutsceneChunkSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;

import java.io.File;
import java.io.IOException;

public class WorldTestPacket implements IPacket
{
	public static final Identifier ID = new Identifier(CutsceneAPI.DOMAIN, "world_test");

	private WorldTest test;

	private WorldTestPacket()
	{

	}

	public WorldTestPacket(WorldTest test)
	{
		this.test = test;
	}

	@Override
	public void read(PacketByteBuf buf)
	{
		test = WorldTest.values()[buf.readInt()];
	}

	@Override
	public void write(PacketByteBuf buf)
	{
		int id = 0;
		for (WorldTest worldTest : WorldTest.values())
		{
			if (worldTest == this.test)
			{
				buf.writeInt(id);
				return;
			}
			id++;
		}
		buf.writeInt(0);
	}

	@Override
	public Identifier getID()
	{
		return ID;
	}

	@Environment(EnvType.CLIENT)
	public static class Handler extends MessageHandler<WorldTestPacket>
	{
		@Override
		protected WorldTestPacket create()
		{
			return new WorldTestPacket();
		}

		@Override
		public void handle(PacketContext ctx, WorldTestPacket message)
		{
			MinecraftClient client = MinecraftClient.getInstance();
			WorldTest test = message.test;
			if (test == WorldTest.JOIN_COPY || test == WorldTest.JOIN_VOID)
			{
				CutsceneManager.startFakeWorld(test == WorldTest.JOIN_COPY);
			} else if (test == WorldTest.LEAVE)
			{
				CutsceneManager.stopFakeWorld();
			} else if (test == WorldTest.SERIALIZE)
			{
				int radius = 15;
				for (int x = -radius; x <= radius; x++)
				{
					for (int z = -radius; z <= radius; z++)
					{
						Chunk chunk = client.world.getChunk(new BlockPos(x * 16, 0, z * 16));
						CutsceneChunkSerializer.serializeAndSave(client.world, chunk);
					}
				}
				client.player.addChatMessage(new TranslatableTextComponent("command.cutsceneapi.serializedchunk"), false);
			} else if (test == WorldTest.DESERIALIZE)
			{
				CompoundTag tag;
				ChunkPos pos = new ChunkPos(0, 0);
				try
				{
					tag = CutsceneAPIClient.STORAGE.getChunkData(pos);
				} catch (IOException e)
				{
					CutsceneAPI.getLogger().error("Failed to deserialize cutscene chunk! Printing stack trace...");
					e.printStackTrace();
					return;
				}
				if (!tag.isEmpty())
				{
					Chunk chunk = CutsceneChunkSerializer.deserialize(client.world, pos, tag);
					CutsceneAPI.getLogger().info("Got chunk! Block at {0, 0, 0}: " + chunk.getBlockState(new BlockPos(pos.x * 16, 0, pos.z * 16)).getBlock());
					client.player.addChatMessage(new TranslatableTextComponent("command.cutsceneapi.deserialized"), false);
				} else
				{
					client.player.addChatMessage(new TranslatableTextComponent("command.cutsceneapi.didntserialize"), false);
				}
			}
		}
	}

	public enum WorldTest
	{
		JOIN_VOID, JOIN_COPY, LEAVE, SERIALIZE, DESERIALIZE
	}
}
