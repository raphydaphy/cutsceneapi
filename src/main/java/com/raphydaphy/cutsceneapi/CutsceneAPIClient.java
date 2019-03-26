package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.network.CutsceneStartPacket;
import com.raphydaphy.cutsceneapi.network.WorldTestPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

public class CutsceneAPIClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		ClientSidePacketRegistry.INSTANCE.register(CutsceneStartPacket.ID, new CutsceneStartPacket.Handler());
		ClientSidePacketRegistry.INSTANCE.register(WorldTestPacket.ID, new WorldTestPacket.Handler());
	}
}
