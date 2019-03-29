package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CutsceneFinishedPacket implements IMessage
{
	public CutsceneFinishedPacket()
	{

	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
	}

	public static class Handler implements IMessageHandler<CutsceneFinishedPacket, IMessage>
	{
		@Override
		public IMessage onMessage(CutsceneFinishedPacket message, MessageContext ctx)
		{
			CutsceneAPI.getCutsceneManager().stop(ctx.getServerHandler().player);
			return null;
		}
	}
}
