package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.crochet.network.IPacket;
import com.raphydaphy.crochet.network.MessageHandler;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneChunk;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import com.raphydaphy.cutsceneapi.fakeworld.storage.CutsceneChunkSerializer;
import com.raphydaphy.cutsceneapi.path.PathRecorder;
import com.raphydaphy.cutsceneapi.path.RecordedPath;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPos;

import java.io.IOException;

public class CutsceneCommandPacket implements IPacket
{
	public static final Identifier ID = new Identifier(CutsceneAPI.DOMAIN, "world_test");

	private Command test;

	private CutsceneCommandPacket()
	{

	}

	public CutsceneCommandPacket(Command test)
	{
		this.test = test;
	}

	@Override
	public void read(PacketByteBuf buf)
	{
		test = Command.values()[buf.readInt()];
	}

	@Override
	public void write(PacketByteBuf buf)
	{
		int id = 0;
		for (Command command : Command.values())
		{
			if (command == this.test)
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
	public static class Handler extends MessageHandler<CutsceneCommandPacket>
	{
		@Override
		protected CutsceneCommandPacket create()
		{
			return new CutsceneCommandPacket();
		}

		@Override
		public void handle(PacketContext ctx, CutsceneCommandPacket message)
		{
			MinecraftClient client = MinecraftClient.getInstance();
			Command command = message.test;
			if (command == Command.JOIN_COPY_WORLD || command == Command.JOIN_VOID_WORLD || command == Command.JOIN_CACHED_WORLD)
			{
				if (command == Command.JOIN_CACHED_WORLD)
				{
					CutsceneWorld cutsceneWorld = new CutsceneWorld(client, client.world, null, false);
					int radius = 15;
					for (int chunkX = -radius; chunkX <= radius; chunkX++)
					{
						for (int chunkZ = -radius; chunkZ <= radius; chunkZ++)
						{
							ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
							CompoundTag chunkData;
							try
							{
								chunkData = CutsceneAPIClient.STORAGE.getChunkData(chunkPos);
							} catch (IOException e)
							{
								CutsceneAPI.getLogger().error("Failed to deserialize cutscene chunk! Printing stack trace...");
								e.printStackTrace();
								continue;
							}
							if (!chunkData.isEmpty())
							{
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
											blockStates[index] = chunk.getBlockState(new BlockPos(chunk.getPos().getStartX() + x, y, chunk.getPos().getStartZ() + z));
										}
									}
								}
								CutsceneChunk cutsceneChunk = new CutsceneChunk(cutsceneWorld, chunkPos, chunk.getBiomeArray(), blockStates);
								cutsceneWorld.putChunk(chunkPos, cutsceneChunk);
							}
						}
					}
					CutsceneManager.startFakeWorld(cutsceneWorld, false);
				} else
				{
					boolean copy = command == Command.JOIN_COPY_WORLD;
					CutsceneManager.startFakeWorld(new CutsceneWorld(client, client.world, null, copy), !copy);
				}
			} else if (command == Command.LEAVE_WORLD)
			{
				CutsceneManager.stopFakeWorld();
			} else if (command == Command.RECORD_CAMERA)
			{
				PathRecorder.start();
				client.player.addChatMessage(new TranslatableTextComponent("command.cutsceneapi.startrecording"), true);
			} else if (command == Command.STOP_RECORDING)
			{
				PathRecorder.stop();
				client.player.addChatMessage(new TranslatableTextComponent("command.cutsceneapi.stoprecording"), true);
			} else if (command == Command.SERIALIZE_WORLD)
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
			} else if (command == Command.DESERIALIZE_WORLD)
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

	public enum Command
	{
		JOIN_VOID_WORLD, JOIN_COPY_WORLD, JOIN_CACHED_WORLD, LEAVE_WORLD, SERIALIZE_WORLD, DESERIALIZE_WORLD, RECORD_CAMERA, STOP_RECORDING
	}
}