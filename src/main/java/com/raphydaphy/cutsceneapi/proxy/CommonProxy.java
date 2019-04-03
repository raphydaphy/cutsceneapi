package com.raphydaphy.cutsceneapi.proxy;

import com.raphydaphy.cutsceneapi.CutsceneMod;
import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.BasicCutsceneManager;
import com.raphydaphy.cutsceneapi.cutscene.DefaultCutscene;
import com.raphydaphy.cutsceneapi.init.ModCutscenes;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishedPacket;
import com.raphydaphy.cutsceneapi.network.CutsceneStartPacket;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy
{
	public void preInit()
	{
		CutsceneAPI.initialize(new BasicCutsceneManager(), DefaultCutscene::new);
		ModCutscenes.setup();
	}

	public void init()
	{
		CutsceneMod.NETWORK_WRAPPER.registerMessage(CutsceneStartPacket.Handler.class, CutsceneStartPacket.class, 0, Side.CLIENT);
		CutsceneMod.NETWORK_WRAPPER.registerMessage(CutsceneFinishedPacket.Handler.class, CutsceneFinishedPacket.class, 1, Side.SERVER);
	}

	public void postInit()
	{
		ModCutscenes.configure();
	}
}
