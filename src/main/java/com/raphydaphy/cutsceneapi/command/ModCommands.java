package com.raphydaphy.cutsceneapi.command;

import static net.minecraft.server.command.CommandManager.*;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.network.ModPackets;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModCommands {

  public static void register() {
    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
      dispatcher.register(literal("cutsceneeditor").executes(context -> {
        ServerPlayNetworking.send(context.getSource().getPlayer(), ModPackets.CUTSCENE_EDITOR_PACKET, PacketByteBufs.empty());
        return 0;
      }));

      dispatcher.register(literal("cutscene").executes(context -> {
        CutsceneAPI.LOGGER.info("Cutscene command called");
        return 0;
      }));
    });
  }
}
