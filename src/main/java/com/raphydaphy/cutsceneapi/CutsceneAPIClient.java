package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.network.ClientModPackets;
import net.fabricmc.api.ClientModInitializer;

public class CutsceneAPIClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ClientModPackets.register();
  }
}
