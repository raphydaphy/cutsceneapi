package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.api.CutsceneEntry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CutsceneRegistry
{
	private static Map<Identifier, Cutscene> REGISTRY = new HashMap<>();

	public static CutsceneEntry register(Identifier id, Cutscene cutscene)
	{
		if (!REGISTRY.containsKey(id))
		{
			cutscene.setID(id);
			REGISTRY.put(id, cutscene);
			return new CutsceneEntry(id, cutscene.getLength());
		}
		CutsceneAPI.getLogger().error("Tried to replace existing cutscene with ID " + id + "! Use CutsceneRegistry#replace if you wish to replace a cutscene.");
		return null;
	}

	public static void replace(CutsceneEntry entry, Cutscene cutscene)
	{
		cutscene.setID(entry.id);
		REGISTRY.put(entry.id, cutscene);
	}

	public static Cutscene get(Identifier id)
	{
		return REGISTRY.get(id).copy();
	}

	public static Set<Identifier> getIDs()
	{
		return REGISTRY.keySet();
	}
}
