package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneMod;
import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.api.CutsceneManager;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BasicCutsceneManager implements CutsceneManager
{
	private static Map<ResourceLocation, Cutscene> REGISTRY = new HashMap<>();

	@Override
	public void register(ResourceLocation id, Cutscene cutscene)
	{
		if (REGISTRY.containsKey(id)) CutsceneMod.getLogger().error("Tried to register a cutscene with ID " + id + ", but it already existed!");
		else REGISTRY.put(id, cutscene);
	}

	@Override
	public Cutscene get(ResourceLocation id)
	{
		return null;
	}

	@Override
	public void start(Cutscene cutscene)
	{

	}

	@Override
	public void stop()
	{

	}
}
