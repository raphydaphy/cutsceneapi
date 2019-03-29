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
	private Map<ResourceLocation, Cutscene> REGISTRY = new HashMap<>();
	private String WATCHING_CUTSCENE_KEY = "WatchingCutscene";
	private Cutscene currentCutscene;

	@Override
	public void register(ResourceLocation id, Cutscene cutscene)
	{
		cutscene.setID(id);
		if (REGISTRY.containsKey(id)) CutsceneMod.getLogger().error("Tried to register a cutscene with ID " + id + ", but it already existed!");
		else REGISTRY.put(id, cutscene);
	}

	@Override
	public Cutscene get(ResourceLocation id)
	{
		return null;
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
		}
	}
}
