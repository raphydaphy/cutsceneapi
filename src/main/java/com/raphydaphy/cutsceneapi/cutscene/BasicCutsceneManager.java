package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneMod;
import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.api.CutsceneManager;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishedPacket;
import com.raphydaphy.cutsceneapi.network.CutsceneStartPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class BasicCutsceneManager implements CutsceneManager
{
	public static String WATCHING_CUTSCENE_KEY = "WatchingCutscene";
	public static String CURRENT_CUTSCENE_KEY = "CurrentCutscene";

	private Map<ResourceLocation, Cutscene> REGISTRY = new HashMap<>();
	private Cutscene currentCutscene;

	@Override
	public void register(ResourceLocation id, Cutscene cutscene)
	{
		System.out.println(("registered cutscene with id " + id.toString()));
		cutscene.setID(id);
		if (REGISTRY.containsKey(id))
			CutsceneMod.getLogger().error("Tried to register a cutscene with ID " + id + ", but it already existed!");
		else REGISTRY.put(id, cutscene);
	}

	@Override
	public Cutscene get(ResourceLocation id, boolean client)
	{
		return REGISTRY.containsKey(id) ? REGISTRY.get(id).copy() : null;
	}

	@Override
	public void start(EntityPlayer player, Cutscene cutscene)
	{
		if (player.world.isRemote)
		{
			this.currentCutscene = cutscene;
		} else
		{
			player.getEntityData().setBoolean(WATCHING_CUTSCENE_KEY, true);
			player.getEntityData().setString(CURRENT_CUTSCENE_KEY, cutscene.getID().toString());
			CutsceneMod.NETWORK_WRAPPER.sendTo(new CutsceneStartPacket(cutscene.getID()), (EntityPlayerMP) player);
		}
	}

	@Override
	public void stop(EntityPlayer player)
	{
		if (player.world.isRemote)
		{
			this.currentCutscene = null;
			CutsceneMod.NETWORK_WRAPPER.sendToServer(new CutsceneFinishedPacket());
		} else
		{
			player.getEntityData().setBoolean(WATCHING_CUTSCENE_KEY, false);
			player.getEntityData().removeTag(CURRENT_CUTSCENE_KEY);
		}
	}

	@Override
	public Cutscene getActiveCutscene(EntityPlayer player)
	{
		if (player.world.isRemote)
		{
			return currentCutscene;
		} else
		{
			return get(new ResourceLocation(player.getEntityData().getString(CURRENT_CUTSCENE_KEY)), false);
		}
	}
}
