package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.crochet.network.IPacket;
import com.raphydaphy.crochet.network.MessageHandler;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class WorldTestPacket implements IPacket
{
	public static final Identifier ID = new Identifier(CutsceneAPI.DOMAIN, "world_test");

	private boolean joining;
	private boolean copy;

	private WorldTestPacket()
	{

	}

	public WorldTestPacket(boolean joining, boolean copy)
	{
		this.joining = joining;
		this.copy = copy;
	}

	@Override
	public void read(PacketByteBuf buf)
	{
		joining = buf.readBoolean();
		copy = buf.readBoolean();
	}

	@Override
	public void write(PacketByteBuf buf)
	{
		buf.writeBoolean(joining);
		buf.writeBoolean(copy);
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
			if (message.joining)
			{
				CutsceneManager.startFakeWorld(message.copy);
			} else
			{
				CutsceneManager.stopFakeWorld();
			}
		}
	}
}
