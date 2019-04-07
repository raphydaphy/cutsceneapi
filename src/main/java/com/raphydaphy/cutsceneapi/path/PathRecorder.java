package com.raphydaphy.cutsceneapi.path;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3f;

@Environment(EnvType.CLIENT)
public class PathRecorder
{
	private static RecordedPath.Builder buildingPath;
	private static boolean recording = false;

	public static void start()
	{
		buildingPath = RecordedPath.builder();
		recording = true;
	}

	public static void stop()
	{
		if (recording)
		{
			if (buildingPath != null)
			{
				CutsceneAPIClient.CACHED_PATH = buildingPath.build();
			} else
			{
				CutsceneAPI.getLogger().error("Tried to stop a cutscene path recording without a valid path builder!");
			}
			recording = false;
		} else
		{
			CutsceneAPI.getLogger().error("Tried to stop a non-existent cutscene path recording!");
		}
	}

	public static boolean isRecording()
	{
		return recording;
	}

	public static void tick()
	{
		if (recording)
		{
			MinecraftClient client = MinecraftClient.getInstance();
			if (buildingPath == null)
			{
				CutsceneAPI.getLogger().error("Tried to a cutscene path record without a valid path builder!");
				recording = false;
			} else
			{
				buildingPath.with(new Vector3f((float) client.player.x, (float) client.player.y, (float) client.player.z), client.player.pitch, client.player.yaw);
			}
		}
	}
}
