package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Iterator;

public class PacketHandler
{
	@Environment(EnvType.CLIENT)
	public static void sendToServer(IPacket packet)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		packet.write(buf);
		ClientPlayNetworkHandler netHandler = MinecraftClient.getInstance().getNetworkHandler();

		if (netHandler != null)
		{
			netHandler.getClientConnection().sendPacket(new CustomPayloadC2SPacket(packet.getID(), buf));
		}
	}

	public static void sendToClient(IPacket packet, ServerPlayerEntity player)
	{
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		packet.write(buf);
		player.networkHandler.sendPacket(new CustomPayloadS2CPacket(packet.getID(), buf));
	}

	public static void sendToAllAround(IPacket packet, World world, BlockPos center, int radius)
	{
		if (world instanceof ServerWorld)
		{
			Iterator<PlayerEntity> iter = PlayerStream.around(world, center, radius).iterator();
			while (iter.hasNext())
			{
				PlayerEntity player = iter.next();
				sendToClient(packet, (ServerPlayerEntity) player);
			}
		} else
		{
			CutsceneAPI.getLogger().warn("Tried to send server packet to client");
		}
	}
}