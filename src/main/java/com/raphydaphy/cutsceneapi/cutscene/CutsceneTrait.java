package com.raphydaphy.cutsceneapi.cutscene;

import me.elucent.earlgray.api.Trait;
import me.elucent.earlgray.api.TraitHolder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

public class CutsceneTrait extends Trait
{
	private boolean watchingCutscene;
	private int length;
	private int time;

	@Override
	public CompoundTag write(CompoundTag tag)
	{
		tag.putBoolean("watchingCutscene", watchingCutscene);
		tag.putInt("length", length);
		tag.putInt("time", time);
		return tag;
	}

	@Override
	public Trait read(CompoundTag tag)
	{
		CutsceneTrait trait = new CutsceneTrait();
		trait.watchingCutscene = tag.getBoolean("watchingCutscene");
		trait.length = tag.getInt("length");
		trait.time = tag.getInt("time");
		return trait;
	}

	public boolean isWatching()
	{
		return watchingCutscene;
	}

	public int getLength()
	{
		return length;
	}

	public int getTime()
	{
		return time;
	}

	public void update(Entity entity)
	{
		if (isWatching())
		{
			if (getTime() >= getLength())
			{
				setWatching(false);
			} else
			{
				this.time--;
				markDirty();
			}
		}
	}

	public void startWatching(int length)
	{
		this.watchingCutscene = true;
		this.length = length;
		this.time = length;
		markDirty();
	}

	public void setWatching(boolean watching)
	{
		this.watchingCutscene = watching;
		markDirty();
	}

	public void setLength(int length)
	{
		this.length = length;
		markDirty();
	}

	public void setTime(int time)
	{
		this.time = time;
		markDirty();
	}
}
