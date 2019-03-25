package com.raphydaphy.cutsceneapi.cutscene;

import com.mojang.blaze3d.platform.GLX;
import com.raphydaphy.crochet.network.PacketHandler;
import com.raphydaphy.cutsceneapi.mixin.client.ClientWorldHooks;
import com.raphydaphy.cutsceneapi.mixin.client.GameRendererHooks;
import com.raphydaphy.cutsceneapi.mixin.client.MinecraftClientHooks;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.LevelInfo;

import java.util.ArrayList;
import java.util.List;

public class Cutscene
{
	private List<Transition> transitionList = new ArrayList<>();
	private CutsceneCameraEntity camera;
	private boolean usesFakeWorld = false;
	private int ticks;
	private int duration;
	private boolean setCamera = false;
	private Identifier shader;
	private Path cameraPath;
	private SoundEvent startSound;
	private ClientWorld realWorld;

	public Cutscene(PlayerEntity player, Path cameraPath)
	{
		this.ticks = 0;
		this.cameraPath = cameraPath.build();
		this.camera = new CutsceneCameraEntity(player.world).withPos(this.cameraPath.getPoint(0));
	}

	public Cutscene withDuration(int duration)
	{
		this.duration = duration;
		return this;
	}

	public Cutscene setFakeWorld()
	{
		this.usesFakeWorld = true;
		return this;
	}

	public Cutscene withStartSound(SoundEvent startSound)
	{
		this.startSound = startSound;
		return this;
	}

	public Cutscene withDipTo(float length, int red, int green, int blue)
	{
		withTransition(new Transition.DipTo(0, length, red, green, blue).setIntro());
		withTransition(new Transition.DipTo(duration - length, length, red, green, blue).setOutro());
		return this;
	}

	public Cutscene withTransition(Transition transition)
	{
		transitionList.add(transition);
		return this;
	}

	public Cutscene withShader(Identifier shader)
	{
		this.shader = shader;
		return this;
	}

	@Environment(EnvType.CLIENT)
	public void start(PlayerEntity player)
	{
		if (startSound != null)
		{
			player.playSound(startSound, 1, 1);
		}
	}

	@Environment(EnvType.CLIENT)
	void updateClient()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (hideHud())
		{
			camera.update();
			camera.moveTo(cameraPath.getPoint(ticks / (float) duration));
			if (!setCamera)
			{
				client.cameraEntity = camera;

				if (this.shader != null)
				{
					client.worldRenderer.method_3292();
					if (GLX.usePostProcess)
					{
						((GameRendererHooks) client.gameRenderer).useShader(this.shader);
					}
				}
				setCamera = true;
			}
			if (realWorld == null && usesFakeWorld)
			{
				realWorld = client.world;
				((MinecraftClientHooks) client).setCutsceneWorld(new CutsceneWorld(((ClientWorldHooks)client.world).getCutsceneNetworkHandler(), new LevelInfo(client.world.getLevelProperties()), client.world.getProfiler(), client.worldRenderer));
			}
		} else
		{
			if (realWorld != null && usesFakeWorld)
			{
				((MinecraftClientHooks) client).setCutsceneWorld(realWorld);
				realWorld = null;
			}

			client.gameRenderer.disableShader();
			setCamera = false;
			client.setCameraEntity(client.player);
			//client.worldRenderer.method_3292();
		}

		if (ticks >= duration)
		{
			PacketHandler.sendToServer(new CutsceneFinishPacket());
		} else
		{
			ticks++;
		}
	}

	void updateLook()
	{
		MinecraftClient client = MinecraftClient.getInstance();

		float interpCutsceneTime = lerp(ticks, ticks + 1, client.getTickDelta());

		for (Transition transition : transitionList)
		{
			if (transition.active(interpCutsceneTime) && transition.fixedCamera)
			{
				return;
			}
		}
		float percent = interpCutsceneTime / (float) duration;

		Vector3f direction = cameraPath.getPoint(percent);
		direction.subtract(cameraPath.getPoint(percent >= 0.99f ? 0.9999f : percent + 0.01f));
		float lengthSquared = direction.x() * direction.x() + direction.y() * direction.y() + direction.z() * direction.z();
		if (lengthSquared != 0 && lengthSquared != 1) direction.scale(1 / (float) Math.sqrt(lengthSquared));

		camera.prevYaw = camera.yaw;
		camera.prevPitch = camera.pitch;

		camera.pitch = (float) Math.toDegrees(Math.asin(direction.y()));
		camera.yaw = (float) Math.toDegrees(Math.atan2(direction.x(), direction.z()));
	}

	@Environment(EnvType.CLIENT)
	void renderTransitions()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		float interpCutsceneTime = lerp(ticks, ticks + 1, client.getTickDelta());

		for (Transition transition : transitionList)
		{
			if (transition.active(interpCutsceneTime))
			{
				transition.render(client, interpCutsceneTime);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	boolean hideHud()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		float interpCutsceneTime = lerp(ticks, ticks + 1, client.getTickDelta());

		for (Transition transition : transitionList)
		{
			if (transition.active(interpCutsceneTime))
			{
				if (transition.showHud())
				{
					return false;
				}
			}
		}

		return true;
	}

	boolean usesFakeWorld()
	{
		return usesFakeWorld;
	}

	private static float lerp(float previous, float current, float delta)
	{
		return (1 - delta) * previous + delta * current;
	}
}
