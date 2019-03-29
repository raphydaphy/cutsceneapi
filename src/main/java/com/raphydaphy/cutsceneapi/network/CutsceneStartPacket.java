package com.raphydaphy.cutsceneapi.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CutsceneStartPacket implements IMessage
{
	private ResourceLocation id;
	private int length;

	public CutsceneStartPacket()
	{

	}

	public CutsceneStartPacket(ResourceLocation id, int length)
	{
		this.id = id;
		this.length = length;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		PacketBuffer pbuf = new PacketBuffer(buf);
		id = new ResourceLocation(pbuf.readString(pbuf.readInt()));
		length = pbuf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		PacketBuffer pbuf = new PacketBuffer(buf);
		String id = this.id.toString();
		pbuf.writeInt(id.length());
		pbuf.writeString(id);
		pbuf.writeInt(length);
	}

	public static class Handler implements IMessageHandler<CutsceneStartPacket, IMessage>
	{
		@Override
		public IMessage onMessage(CutsceneStartPacket message, MessageContext ctx)
		{
			return null;
		}
	}
}
