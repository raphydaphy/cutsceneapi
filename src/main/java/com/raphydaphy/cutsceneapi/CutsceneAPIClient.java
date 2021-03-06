package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.editor.CutsceneEditor;
import com.raphydaphy.cutsceneapi.entity.ModEntities;
import com.raphydaphy.cutsceneapi.network.ClientModPackets;
import net.fabricmc.api.ClientModInitializer;

public class CutsceneAPIClient implements ClientModInitializer {
  public static CutsceneEditor EDITOR = null;

  @Override
  public void onInitializeClient() {
    ClientModPackets.register();
    ModEntities.registerRenderers();
  }

  public static boolean isEditorOpen() {
    return EDITOR != null;
  }
}
