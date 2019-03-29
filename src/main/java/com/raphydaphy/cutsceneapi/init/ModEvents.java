package com.raphydaphy.cutsceneapi.init;

import com.raphydaphy.cutsceneapi.CutsceneMod;
import com.raphydaphy.cutsceneapi.camera.CutsceneCamera;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

@Mod.EventBusSubscriber()
public class ModEvents
{
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityEntry> event)
	{
		event.getRegistry().register(new EntityEntry(CutsceneCamera.class, CutsceneMod.MODID + ":camera"));
	}
}
