package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.api.Cutscene;

public class DefaultCutscene implements Cutscene
{
	private int length;

	@Override
	public void setLength(int length)
	{
		this.length = length;
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
		cutscene.setLength(getLength());
		return cutscene;
	}
}
