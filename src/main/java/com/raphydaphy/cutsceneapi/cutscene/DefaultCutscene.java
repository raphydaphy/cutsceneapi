package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.api.Cutscene;
import net.minecraft.util.ResourceLocation;

public class DefaultCutscene implements Cutscene
{
	private ResourceLocation id;
	private int length;

	@Override
	public void setID(ResourceLocation id)
	{
		this.id = id;
	}

	@Override
	public void setLength(int length)
	{
		this.length = length;
	}

	@Override
	public ResourceLocation getID()
	{
		return id;
	}

	@Override
	public int getLength()
	{
		return length;
	}

	@Override
	public DefaultCutscene copy()
	{
		DefaultCutscene cutscene = new DefaultCutscene();
		cutscene.setID(getID());
		cutscene.setLength(getLength());
		return cutscene;
	}
}
