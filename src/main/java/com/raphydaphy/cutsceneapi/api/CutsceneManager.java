package com.raphydaphy.cutsceneapi.api;

import jline.internal.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

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
	Cutscene get(ResourceLocation id, boolean client);

	/**
	 * Start playing a cutscene. Should be called on the server.
	 * #param player The player who should watch the cutscene
	 * @param cutscene The cutscene which should be started
	 */
	void start(EntityPlayer player, Cutscene cutscene);

	/**
	 * Stops a cutscene. Should be called on the client.
	 */
	void stop(EntityPlayer player);

	/**
	 * @return The cutscene that is currently playing
	 */
	@Nullable
	Cutscene getActiveCutscene(EntityPlayer player);
}
