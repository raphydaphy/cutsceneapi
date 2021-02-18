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
        CutsceneAPI.LOGGER.info("Cutscene editor triggered on client");
        if (CutsceneAPIClient.EDITOR != null) {
          CutsceneAPI.LOGGER.warn("Tried to open cutscene editor when it was already open");
          return;
        }

        MinecraftClient.getInstance().openPauseMenu(true);
        CutsceneAPIClient.EDITOR = new CutsceneEditor();
      });
    });
  }
}
