package com.raphydaphy.cutsceneapi.init;

import com.raphydaphy.cutsceneapi.CutsceneMod;
import com.raphydaphy.cutsceneapi.api.ClientCutscene;
import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModCutscenes
{
	public static Cutscene DEMO;

	public static void setup()
	{
		DEMO = CutsceneAPI.createCutscene();
	}

	@SideOnly(Side.CLIENT)
	public static void setupClient()
	{
		DEMO = CutsceneAPI.createClientCutscene();
	}

	public static void configure()
	{
		CutsceneAPI.getCutsceneManager().register(new ResourceLocation(CutsceneMod.MODID, "demo"), DEMO);
		DEMO.setLength(100);
		DEMO.setInitCallback((cutscene) -> {
			System.out.println("Started demo cutscene on both sides!");
		});
	}

	@SideOnly(Side.CLIENT)
	public static void configureClient()
	{
		ClientCutscene demo = (ClientCutscene)DEMO;
		demo.setClientInitCallback((cutscene) -> {
			Minecraft minecrafT = Minecraft.getMinecraft();
			System.out.println("Started demo cutscene on client side!");
		});
	}
}
