package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CutsceneStartPacket implements IPacket
{
	public static final Identifier ID = new Identifier(CutsceneAPI.DOMAIN, "cutscene_start");

	public CutsceneStartPacket()
	{

	}

	@Override
	public void read(PacketByteBuf buf)
	{
	}

	@Override
	public void write(PacketByteBuf buf)
	{
	}

	@Override
	public Identifier getID()
	{
		return ID;
	}

	public static class Handler extends MessageHandler<CutsceneStartPacket>
	{
		@Override
		protected CutsceneStartPacket create()
		{
			return new CutsceneStartPacket();
		}

		@Override
		public void handle(PacketContext ctx, CutsceneStartPacket message)
		{
			CutsceneManager.startClient();
		}
	}
}
