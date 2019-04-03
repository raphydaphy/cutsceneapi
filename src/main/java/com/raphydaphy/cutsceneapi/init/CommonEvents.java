package com.raphydaphy.cutsceneapi.init;

import com.raphydaphy.cutsceneapi.CutsceneMod;
import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import com.raphydaphy.cutsceneapi.camera.CutsceneCamera;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

@Mod.EventBusSubscriber()
public class CommonEvents
{
	private static final String CUTSCENE_CAMERA = CutsceneMod.MODID + ":camera";

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
	{
		event.getRegistry().register(new EntityEntry(CutsceneCamera.class, CUTSCENE_CAMERA).setRegistryName(CUTSCENE_CAMERA));
	}

	@SubscribeEvent
	public static void tick(TickEvent.PlayerTickEvent event)
	{
		Cutscene currentCutscene = CutsceneAPI.getCutsceneManager().getActiveCutscene(event.player);
		if (currentCutscene != null)
		{
			currentCutscene.tick();
		}
	}
}
