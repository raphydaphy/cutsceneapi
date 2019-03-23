package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.network.CutsceneStartPacket;
import com.raphydaphy.cutsceneapi.network.PacketHandler;
import me.elucent.earlgray.api.Traits;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class CutsceneManager
{
	private static Cutscene currentCutscene;

	public static boolean hideHud(PlayerEntity player)
	{
		return isActive(player) && currentCutscene != null && currentCutscene.hideHud();
	}

	public static boolean isActive(PlayerEntity player)
	{
		return player != null && Traits.has(player, CutsceneAPI.CUTSCENE_TRAIT) && Traits.get(player, CutsceneAPI.CUTSCENE_TRAIT).isWatching();
	}

	@Environment(EnvType.CLIENT)
	public static void updateLook()
	{
		if (currentCutscene != null)
		{
			currentCutscene.updateLook();
		}
	}

	@Environment(EnvType.CLIENT)
	public static void renderHud()
	{
		if (currentCutscene != null)
		{
			currentCutscene.renderTransitions();
		}
	}

	@Environment(EnvType.CLIENT)
	public static void startClient()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		client.player.playSound(SoundEvents.UI_BUTTON_CLICK, 1, 1);
		float pX = (float)client.player.getBlockPos().getX();
		float pY = (float)client.player.getBlockPos().getY();
		float pZ = (float)client.player.getBlockPos().getZ();

		currentCutscene = new Cutscene(client.player, new Path().withPoint(pX + 0, pY + 20, pZ + 0).withPoint(pX + 30, pY + 30, pZ + 10).withPoint(pX + 50, pY + 10, pZ + 10))
				.withDipTo(20, 255, 255, 255);
	}

	@Environment(EnvType.CLIENT)
	public static void updateClient()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (isActive(client.player) && currentCutscene != null)
		{
			currentCutscene.updateClient();
		}
	}

	public static void startServer(ServerPlayerEntity player, int duration)
	{
		player.stopRiding();
		Traits.get(player, CutsceneAPI.CUTSCENE_TRAIT).startWatching(duration);
		PacketHandler.sendToClient(new CutsceneStartPacket(), player);
	}

	public static void finishServer(PlayerEntity player)
	{
		Traits.get(player, CutsceneAPI.CUTSCENE_TRAIT).setWatching(false);
	}
}
