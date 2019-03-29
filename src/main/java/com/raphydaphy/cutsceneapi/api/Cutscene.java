package com.raphydaphy.cutsceneapi.api;

import net.minecraft.util.ResourceLocation;

public interface Cutscene
{
	/**
	 * @param id The ID which should be used to register the cutscene
	 */
	void setID(ResourceLocation id);

	/**
	 * @param length The new length in ticks
	 */
	void setLength(int length);

	/**
	 * @param path The path which the camera should follow during the cutscene
	 */
	void setPath(Path path);

	/**
	 * @return The registry ID of the cutscene
	 */
	ResourceLocation getID();

	/**
	 * @return The cutscene length in ticks
	 */
	int getLength();

	/**
	 * @return A copy of the cutscene
	 */
	Cutscene copy();
}
