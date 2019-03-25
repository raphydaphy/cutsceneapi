package com.raphydaphy.cutsceneapi.cutscene;

import com.mojang.blaze3d.platform.GLX;
import com.raphydaphy.crochet.network.PacketHandler;
import com.raphydaphy.cutsceneapi.mixin.client.GameRendererHooks;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Cutscene
{
	// Settings
	private List<Transition> transitionList = new ArrayList<>();
	private CutsceneCameraEntity camera;
	private Identifier shader;
	private Path cameraPath;
	private SoundEvent startSound;
	private BiFunction<BlockPos, BlockState, BlockState> blockRemapper;
	private Function<BlockPos, FluidState> fluidRemapper;
	private Cutscene nextCutscene;
	private boolean usesFakeWorld = false;
	private int duration;
	private int introLength = 0;
	private int outroLength = 0;

	// Data
	private boolean setFakeWorld = false;
	private boolean setCamera = false;
	private int ticks;
	private int startPerspective;
	private float startPitch;
	private float startYaw;

	public Cutscene(PlayerEntity player, Path cameraPath)
	{
		this.ticks = 0;
		this.cameraPath = cameraPath.build();
		this.camera = new CutsceneCameraEntity(player.world).withPos(this.cameraPath.getPoint(0));
		fluidRemapper = (pos) -> Fluids.EMPTY.getDefaultState();
		blockRemapper = (pos, state) -> Blocks.AIR.getDefaultState();
	}

	public Cutscene withDuration(int ticks)
	{
		this.duration = ticks;
		return this;
	}

	public Cutscene withFakeWorld()
	{
		this.usesFakeWorld = true;
		return this;
	}

	public Cutscene withBlockRemapper(BiFunction<BlockPos, BlockState, BlockState> remapFunction)
	{
		this.blockRemapper = remapFunction;
		return this;
	}

	public Cutscene withFluidRemapper(Function<BlockPos, FluidState> remapFunction)
	{
		this.fluidRemapper = remapFunction;
		return this;
	}

	public Cutscene withStartSound(SoundEvent startSound)
	{
		this.startSound = startSound;
		return this;
	}

	public Cutscene withDipTo(float length, float hold, int red, int green, int blue)
	{
		this.introLength = (int)Math.floor((length - hold) / 2f);
		this.outroLength = introLength + (int)hold;
		withTransition(new Transition.DipTo(0, length, hold, red, green, blue).setIntro());
		withTransition(new Transition.DipTo(duration - length - hold, length, hold, red, green, blue).setOutro());
		return this;
	}

	public Cutscene withTransition(Transition transition)
	{
		transitionList.add(transition);
		return this;
	}

	public Cutscene withCutscene(Cutscene next)
	{
		this.nextCutscene = next;
		return this;
	}

	public BiFunction<BlockPos, BlockState, BlockState> getBlockRemapper()
	{
		return blockRemapper;
	}

	public Function<BlockPos, FluidState> getFluidRemapper()
	{
		return fluidRemapper;
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
		startPerspective = MinecraftClient.getInstance().options.perspective;
		this.startPitch = player.pitch;
		this.startYaw = player.yaw;
	}

	@Environment(EnvType.CLIENT)
	void updateClient()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (hideHud() && ticks > introLength)
		{
			camera.update();
			camera.moveTo(cameraPath.getPoint(ticks / (float) duration));

			if (client.options.perspective != 0)
			{
				client.options.perspective = 0;
				client.worldRenderer.method_3292();
			}

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


				if (usesFakeWorld && !setFakeWorld && ticks >= introLength)
				{
					setFakeWorld = true;
					MinecraftClient.getInstance().worldRenderer.reload();
				}
			}
		} else
		{
			client.gameRenderer.disableShader();
			setCamera = false;
			client.setCameraEntity(client.player);
			client.worldRenderer.method_3292();
			if (usesFakeWorld && setFakeWorld && ticks >= duration - outroLength)
			{
				setFakeWorld = false;
				MinecraftClient.getInstance().worldRenderer.reload();
			}

			if (client.options.perspective != startPerspective)
			{
				client.options.perspective = startPerspective;
				client.worldRenderer.method_3292();
			}
		}

		if (ticks >= duration)
		{
			if (nextCutscene != null)
			{
				this.transitionList = nextCutscene.transitionList;
				this.shader = nextCutscene.shader;
				this.cameraPath = nextCutscene.cameraPath;
				this.startSound = nextCutscene.startSound;
				this.blockRemapper = nextCutscene.blockRemapper;
				this.fluidRemapper = nextCutscene.fluidRemapper;
				this.usesFakeWorld = nextCutscene.usesFakeWorld;
				this.duration = nextCutscene.duration;
				this.outroLength = nextCutscene.outroLength;
				this.introLength = nextCutscene.introLength;

				this.setCamera = true;
				this.setFakeWorld = false;
				this.ticks = 10;

				this.nextCutscene = nextCutscene.nextCutscene;
			} else
			{
				CutsceneManager.finishClient();
			}
		} else
		{
			ticks++;
		}
	}

	void updateLook()
	{
		MinecraftClient client = MinecraftClient.getInstance();

		float interpCutsceneTime = lerp(ticks, ticks + 1, client.getTickDelta());

		client.player.pitch = startPitch;
		client.player.yaw = startYaw;

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
		return usesFakeWorld && ticks >= introLength;
	}

	private static float lerp(float previous, float current, float delta)
	{
		return (1 - delta) * previous + delta * current;
	}
}
