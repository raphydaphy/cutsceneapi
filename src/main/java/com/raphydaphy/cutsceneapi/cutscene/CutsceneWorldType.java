package com.raphydaphy.cutsceneapi.cutscene;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum CutsceneWorldType
{
	/**
	 * The same as a vanilla world.
	 * No special changes will be made.
	 */
	REAL,
	/**
	 * An empty void world.
	 * You can add blocks to it before or during the cutscene.
	 */
	EMPTY,
	/**
	 * A fake world which is based on the real world.
	 * You can modify the blocks without changing the actual world.
	 */
	CLONE;

	public boolean isRealWorld()
	{
		return this == REAL;
	}
}
