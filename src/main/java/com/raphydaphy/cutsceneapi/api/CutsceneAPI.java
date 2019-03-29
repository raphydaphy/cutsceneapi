package com.raphydaphy.cutsceneapi.api;

import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public class CutsceneAPI
{
	private static CutsceneManager cutsceneManager;
	private static Supplier<Cutscene> defaultCutscene;
	private static Supplier<Path> defaultPath;

	/**
	 * @return The default Cutscene Manager
	 */
	public static CutsceneManager getCutsceneManager()
	{
		return cutsceneManager;
	}

	/**
	 * @return A basic cutscene which can be customized
	 */
	public static Cutscene createCutscene()
	{
		return defaultCutscene.get();
	}

	/**
	 * @return A basic path instance
	 */
	public static Path createPath()
	{
		return defaultPath.get();
	}

	/**
	 * Initializes CutsceneAPI's internal accessors
	 * You should not call this method unless you know what you are doing!!
	 */
	public static void initialize(CutsceneManager manager, Supplier<Cutscene> cutscene, Supplier<Path> path)
	{
		cutsceneManager = manager;
		defaultCutscene = cutscene;
		defaultPath = path;
	}
}
