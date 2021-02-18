package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public class ClientModPackets {
  public static void register() {
    ClientPlayNetworking.registerGlobalReceiver(ModPackets.CUTSCENE_EDITOR_PACKET, (client, handler, buf, responseSender) -> {
      client.execute(() -> {
        CutsceneAPI.LOGGER.info("Cutscene editor triggered on client (is paused? " + MinecraftClient.getInstance().isPaused() + ")");

      });
    });
  }
}
