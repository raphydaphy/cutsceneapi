package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.api.Path;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class DefaultCutscene implements Cutscene
{
	// Settings
	protected ResourceLocation id;
	protected int length;
	protected Consumer<Cutscene> initCallback;
	protected Consumer<Cutscene> tickCallback;
	protected Consumer<Cutscene> endCallback;

	// Data
	protected int ticks;

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
	public void setInitCallback(Consumer<Cutscene> callback)
	{
		this.initCallback = callback;
	}

	@Override
	public void setTickCallback(Consumer<Cutscene> callback)
	{
		this.tickCallback = callback;
	}

	@Override
	public void setEndCallback(Consumer<Cutscene> callback)
	{
		this.endCallback = callback;
	}

	@Override
	public void tick()
	{
		if (ticks < length)
		{
			if (ticks == 0)
			{
				if (initCallback != null) initCallback.accept(this);
			}

			if (tickCallback != null) tickCallback.accept(this);

			ticks++;

			if (ticks == length)
			{
				if (endCallback != null) endCallback.accept(this);
			}
		}
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
		applySettings(cutscene);
		return cutscene;
	}

	protected void applySettings(Cutscene cutscene)
	{
		cutscene.setID(id);
		cutscene.setLength(length);
		cutscene.setInitCallback(initCallback);
		cutscene.setTickCallback(tickCallback);
		cutscene.setEndCallback(endCallback);
	}

}
