package com.raphydaphy.cutsceneapi.init;

import com.raphydaphy.cutsceneapi.api.ClientCutscene;
import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEvents
{
	@SubscribeEvent
	public static void render(TickEvent.RenderTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			Minecraft minecraft = Minecraft.getMinecraft();
			if (minecraft.player != null)
			{
				ClientCutscene currentCutscene = (ClientCutscene)CutsceneAPI.getCutsceneManager().getActiveCutscene(minecraft.player);
				if (currentCutscene != null)
				{
					currentCutscene.render();
				}
			}
		}
	}

	@SubscribeEvent
	public static void tick(TickEvent.PlayerTickEvent event)
	{
		ClientCutscene currentCutscene = (ClientCutscene)CutsceneAPI.getCutsceneManager().getActiveCutscene(event.player);
		if (currentCutscene != null)
		{
			currentCutscene.clientTick();
		}
	}
}
