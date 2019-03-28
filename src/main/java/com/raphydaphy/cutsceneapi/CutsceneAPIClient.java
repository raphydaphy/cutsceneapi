package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.cutscene.Path;
import com.raphydaphy.cutsceneapi.cutscene.Transition;
import com.raphydaphy.cutsceneapi.network.CutsceneStartPacket;
import com.raphydaphy.cutsceneapi.network.WorldTestPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;

public class CutsceneAPIClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ClientSidePacketRegistry.INSTANCE.register(CutsceneStartPacket.ID, new CutsceneStartPacket.Handler());
		ClientSidePacketRegistry.INSTANCE.register(WorldTestPacket.ID, new WorldTestPacket.Handler());

		CutsceneAPI.DEMO_CUTSCENE.setIntroTransition(new Transition.DipTo(40, 5, 0, 0, 0));
		CutsceneAPI.DEMO_CUTSCENE.setOutroTransition(new Transition.DipTo(40, 5, 0, 0, 0));
		CutsceneAPI.DEMO_CUTSCENE.setInitCallback((cutscene) -> {
			MinecraftClient client = MinecraftClient.getInstance();
			float playerX = (float)client.player.x;
			float playerY = (float)client.player.y;
			float playerZ = (float)client.player.z;
			cutscene.setCameraPath(new Path()
					.withPoint(playerX - 40, playerY + 35, playerZ).withPoint(playerX + 70, playerY +10, playerZ));
			client.player.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 1, 1);
		});
	}
}
