package com.raphydaphy.cutsceneapi.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

public class CutsceneAPI
{
	private static CutsceneManager cutsceneManager;
	private static Supplier<Cutscene> defaultCutscene;
	@SideOnly(Side.CLIENT)
	private static Supplier<ClientCutscene> defaultClientCutscene;
	@SideOnly(Side.CLIENT)
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
	 * @return a basic client cutscene, ready for customization
	 */
	@SideOnly(Side.CLIENT)
	public static ClientCutscene createClientCutscene()
	{
		return defaultClientCutscene.get();
	}

	/**
	 * @return A basic path instance
	 */
	@SideOnly(Side.CLIENT)
	public static Path createPath()
	{
		return defaultPath.get();
	}

	/**
	 * Initializes CutsceneAPI's internal accessors
	 * You should not call this method unless you know what you are doing!!
	 */
	public static void initialize(CutsceneManager manager, Supplier<Cutscene> cutscene)
	{
		cutsceneManager = manager;
		defaultCutscene = cutscene;
	}

	/**
	 * Initializes CutsceneAPI's client-side accessors
	 * Don't call this unless you know what you are doing!
	 */
	public static void initializeClient(Supplier<ClientCutscene> clientCutscene, Supplier<Path> path)
	{
		defaultClientCutscene = clientCutscene;
		defaultPath = path;
	}
}
