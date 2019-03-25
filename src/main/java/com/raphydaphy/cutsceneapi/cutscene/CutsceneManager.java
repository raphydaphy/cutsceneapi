package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.crochet.data.PlayerData;
import com.raphydaphy.crochet.network.PacketHandler;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.network.CutsceneStartPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
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
		return player != null && PlayerData.get(player).getBoolean(CutsceneAPI.WATCHING_CUTSCENE_KEY);
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
	public static void startClient(Identifier cutscene)
	{
		MinecraftClient client = MinecraftClient.getInstance();

		currentCutscene = CutsceneRegistry.get(cutscene, client.player);
		currentCutscene.start(client.player);
	}

	@Environment(EnvType.CLIENT)
	public static void updateClient()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (isActive(client.player))
		{
			if (currentCutscene == null) currentCutscene = CutsceneRegistry.get(Identifier.create(PlayerData.get(client.player).getString(CutsceneAPI.CUTSCENE_ID_KEY)), client.player);
			if (currentCutscene != null) currentCutscene.updateClient();
		}
	}

	public static void startServer(ServerPlayerEntity player, Identifier id)
	{
		player.stopRiding();
		PlayerData.get(player).putBoolean(CutsceneAPI.WATCHING_CUTSCENE_KEY, true);
		PlayerData.get(player).putString(CutsceneAPI.CUTSCENE_ID_KEY, id.toString());
		PlayerData.markDirty(player);
		PacketHandler.sendToClient(new CutsceneStartPacket(id), player);
	}

	public static void finishServer(PlayerEntity player)
	{
		PlayerData.get(player).putBoolean(CutsceneAPI.WATCHING_CUTSCENE_KEY, false);
		PlayerData.markDirty(player);
	}
}
