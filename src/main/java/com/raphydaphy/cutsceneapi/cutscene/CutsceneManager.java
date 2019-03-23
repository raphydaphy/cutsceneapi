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
		if (isActive(client.player) && currentCutscene != null)
		{
			currentCutscene.updateClient();
		}
	}

	public static void startServer(ServerPlayerEntity player, Identifier id)
	{
		player.stopRiding();
		Traits.get(player, CutsceneAPI.CUTSCENE_TRAIT).start(id);
		PacketHandler.sendToClient(new CutsceneStartPacket(id), player);
	}

	public static void finishServer(PlayerEntity player)
	{
		Traits.get(player, CutsceneAPI.CUTSCENE_TRAIT).setWatching(false);
	}
}
