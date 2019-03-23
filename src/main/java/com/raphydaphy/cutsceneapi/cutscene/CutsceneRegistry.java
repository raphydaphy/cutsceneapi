package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class CutsceneRegistry
{
	private static Map<Identifier, Function<PlayerEntity, Cutscene>> REGISTRY = new HashMap<>();

	public static Identifier register(Identifier id, Function<PlayerEntity, Cutscene> cutscene)
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

	public static Cutscene get(Identifier id, PlayerEntity player)
	{
		return REGISTRY.get(id).apply(player);
	}

	public static Set<Identifier> getIDs()
	{
		return REGISTRY.keySet();
	}
}
