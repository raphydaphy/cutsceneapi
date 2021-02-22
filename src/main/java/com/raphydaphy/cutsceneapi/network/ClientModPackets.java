package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import com.raphydaphy.cutsceneapi.editor.CutsceneEditor;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class ClientModPackets {
  public static void register() {
    ClientPlayNetworking.registerGlobalReceiver(ModPackets.CUTSCENE_EDITOR_PACKET, (client, handler, buf, responseSender) -> {
      client.execute(() -> {
        if (CutsceneAPIClient.EDITOR != null) {
          CutsceneAPI.LOGGER.info("Closing Cutscene Editor");
          CutsceneAPIClient.EDITOR.close();;
          CutsceneAPIClient.EDITOR = null;
          return;
        }

        CutsceneAPI.LOGGER.info("Opening Cutscene Editor");
        CutsceneAPIClient.EDITOR = new CutsceneEditor();
      });
    });
  }
}
