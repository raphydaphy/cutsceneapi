package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.command.ModCommands;
import com.raphydaphy.cutsceneapi.entity.ModEntities;
import com.raphydaphy.cutsceneapi.network.ModPackets;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CutsceneAPI implements ModInitializer {
  public static String MODID = "cutsceneapi";
  public static final Logger LOGGER = LogManager.getLogger("Cutscene API");

  @Override
  public void onInitialize() {
    ModEntities.register();
    ModPackets.register();
    ModCommands.register();
  }
}
