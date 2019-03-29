package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.BasicCutsceneManager;
import com.raphydaphy.cutsceneapi.cutscene.DefaultCutscene;
import com.raphydaphy.cutsceneapi.path.SplinePath;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = CutsceneMod.MODID, version = CutsceneMod.VERSION)
public class CutsceneMod
{
    public static final String MODID = "cutsceneapi";
    public static final String VERSION = "1.0";
    private static final Logger LOGGER = LogManager.getLogger();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	CutsceneAPI.initialize(new BasicCutsceneManager(), DefaultCutscene::new, SplinePath::new);
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
