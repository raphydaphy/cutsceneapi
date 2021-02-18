package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.editor.CutsceneEditor;
import com.raphydaphy.cutsceneapi.network.ClientModPackets;
import net.fabricmc.api.ClientModInitializer;

public class CutsceneAPIClient implements ClientModInitializer {
  public static CutsceneEditor EDITOR = null;

  @Override
  public void onInitializeClient() {
    ClientModPackets.register();
  }
}
