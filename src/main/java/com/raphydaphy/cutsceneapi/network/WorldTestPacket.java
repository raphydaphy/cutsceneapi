package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.crochet.network.IPacket;
import com.raphydaphy.crochet.network.MessageHandler;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.fakeworld.storage.CutsceneChunkSerializer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

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
				File file = new File("cutscene_chunk.mca");
				try
				{
					if (!file.exists())
					{
						file.createNewFile();
					}
				} catch (IOException e)
				{
					CutsceneAPI.getLogger().error("Failed to serialize cutscene chunk! Printing stack trace...");
					e.printStackTrace();
					return;
				}
				client.player.addChatMessage(new TranslatableTextComponent("command.cutsceneapi.serializedchunk"), false);
				CutsceneChunkSerializer.serializeAndSave(file, client.world, client.world.getChunk(client.player.getBlockPos()));
			}
		}
	}

	public enum WorldTest
	{
		JOIN_VOID, JOIN_COPY, LEAVE, SERIALIZE
	}
}
