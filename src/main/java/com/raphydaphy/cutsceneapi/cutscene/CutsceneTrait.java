package com.raphydaphy.cutsceneapi.cutscene;

import me.elucent.earlgray.api.Trait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class CutsceneTrait extends Trait
{
	private boolean watchingCutscene;
	private Identifier cutscene;

	@Override
	public CompoundTag write(CompoundTag tag)
	{
		tag.putBoolean("Watching", watchingCutscene);
		if (watchingCutscene && cutscene != null)
		{
			tag.putString("Cutscene", cutscene.toString());
		}
		return tag;
	}

	@Override
	public Trait read(CompoundTag tag)
	{
		CutsceneTrait trait = new CutsceneTrait();
		trait.watchingCutscene = tag.getBoolean("Watching");
		if (trait.watchingCutscene && tag.containsKey("Cutscene"))
		{
			trait.cutscene = Identifier.create(tag.getString("Cutscene"));
		}
		return trait;
	}

	public void start(Identifier cutscene)
	{
		this.watchingCutscene = true;
		this.cutscene = cutscene;
		markDirty();
	}

	public boolean isWatching()
	{
		return watchingCutscene;
	}

	public Identifier getCutscene()
	{
		return cutscene;
	}

	public void setWatching(boolean watching)
	{
		this.watchingCutscene = watching;
		markDirty();
	}

	public void setCutscene(Identifier cutscene)
	{
		this.cutscene = cutscene;
		markDirty();
	}
}
