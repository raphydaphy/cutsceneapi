package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.BasicCutsceneManager;
import com.raphydaphy.cutsceneapi.cutscene.DefaultCutscene;
import com.raphydaphy.cutsceneapi.network.CutsceneCommand;
import com.raphydaphy.cutsceneapi.path.SplinePath;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = CutsceneMod.MODID, version = "1.0")
public class CutsceneMod
{
	public static final String MODID = "cutsceneapi";
	private static final Logger LOGGER = LogManager.getLogger();
	public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		CutsceneAPI.initialize(new BasicCutsceneManager(), DefaultCutscene::new, SplinePath::new);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CutsceneCommand());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{

	}

	public static Logger getLogger()
	{
		return LOGGER;
	}
}
