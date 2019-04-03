package com.raphydaphy.cutsceneapi.proxy;

import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.DefaultClientCutscene;
import com.raphydaphy.cutsceneapi.init.ModCutscenes;
import com.raphydaphy.cutsceneapi.path.SplinePath;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit()
	{
		CutsceneAPI.initializeClient(DefaultClientCutscene::new, SplinePath::new);
		super.preInit();
	}

	@Override
	public void init()
	{
		super.init();
		ModCutscenes.setupClient();
	}

	@Override
	public void postInit()
	{
		super.postInit();
		ModCutscenes.configureClient();
	}
}
