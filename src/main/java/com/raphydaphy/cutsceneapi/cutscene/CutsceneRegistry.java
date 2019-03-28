package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CutsceneRegistry
{
	private static Map<Identifier, ICutscene> REGISTRY = new HashMap<>();

	public static Identifier register(Identifier id, ICutscene cutscene)
	{
		if (REGISTRY.containsKey(id))
		{
			CutsceneAPI.getLogger().error("Tried to register a cutscene with ID " + id + ", but it already existed!");
			return null;
		} else
		{
			REGISTRY.put(id, cutscene);
			return id;
		}
	}

	public static ICutscene get(Identifier id)
	{
		return REGISTRY.get(id).copy();
	}

	public static Set<Identifier> getIDs()
	{
		return REGISTRY.keySet();
	}
}
