package com.raphydaphy.cutsceneapi.api;

import jline.internal.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface CutsceneManager
{
	/**
	 * Register a cutscene
	 * @param id The ID which the cutscene should be stored by
	 * @param cutscene The cutscene object
	 */
	void register(ResourceLocation id, Cutscene cutscene);

	/**
	 * Get a cutscene from the registry
	 * @param id The ID of the cutscene you want to get
	 * @return The cutscene, if found, or null if an invalid ID is provided
	 */
	@Nullable
	Cutscene get(ResourceLocation id);

	/**
	 * Starts a cutscene on the server
	 * @param cutscene The cutscene which should be started
	 */
	void start(Cutscene cutscene);

	/**
	 * Stops a cutscene on the client
	 */
	@SideOnly(Side.CLIENT)
	void stop();
}
