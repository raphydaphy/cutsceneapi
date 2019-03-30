package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import com.raphydaphy.cutsceneapi.api.CutsceneManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CutsceneStartPacket implements IMessage
{
	private ResourceLocation id;

	public CutsceneStartPacket()
	{

	}

	public CutsceneStartPacket(ResourceLocation id)
	{
		this.id = id;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		PacketBuffer pbuf = new PacketBuffer(buf);
		id = new ResourceLocation(pbuf.readString(pbuf.readInt()));
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		PacketBuffer pbuf = new PacketBuffer(buf);
		String id = this.id.toString();
		pbuf.writeInt(id.length());
		pbuf.writeString(id);
	}

	public static class Handler implements IMessageHandler<CutsceneStartPacket, IMessage>
	{
		@Override
		public IMessage onMessage(CutsceneStartPacket message, MessageContext ctx)
		{
			Minecraft minecraft = Minecraft.getMinecraft();
			CutsceneManager manager = CutsceneAPI.getCutsceneManager();
			manager.start(minecraft.player, manager.get(message.id, true));
			return null;
		}
	}
}
