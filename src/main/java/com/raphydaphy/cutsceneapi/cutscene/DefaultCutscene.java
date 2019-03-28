package com.raphydaphy.cutsceneapi.cutscene;

import com.mojang.blaze3d.platform.GLX;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneChunk;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import com.raphydaphy.cutsceneapi.mixin.client.ClientPlayNetworkHandlerHooks;
import com.raphydaphy.cutsceneapi.mixin.client.GameRendererHooks;
import com.raphydaphy.cutsceneapi.mixin.client.MinecraftClientHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class DefaultCutscene implements Cutscene
{
	// Common Settings
	private int length;

	// Client Settings
	@Environment(EnvType.CLIENT)
	private Transition introTransition;

	@Environment(EnvType.CLIENT)
	private Transition outroTransition;

	@Environment(EnvType.CLIENT)
	private Identifier shader;

	@Environment(EnvType.CLIENT)
	private Consumer<Cutscene> initCallback;

	@Environment(EnvType.CLIENT)
	private Consumer<CutsceneChunk> chunkGenCallback;

	@Environment(EnvType.CLIENT)
	private Consumer<Cutscene> tickCallback;

	@Environment(EnvType.CLIENT)
	private Consumer<Cutscene> renderCallback;

	@Environment(EnvType.CLIENT)
	private Consumer<Cutscene> finishCallback;

	@Environment(EnvType.CLIENT)
	private Path path;

	@Environment(EnvType.CLIENT)
	private CutsceneWorldType worldType = CutsceneWorldType.REAL;

	// Client Data
	@Environment(EnvType.CLIENT)
	private int ticks = 0;

	@Environment(EnvType.CLIENT)
	private CutsceneCameraEntity camera;

	@Environment(EnvType.CLIENT)
	private CutsceneWorld cutsceneWorld;

	@Environment(EnvType.CLIENT)
	private int startPerspective;

	@Environment(EnvType.CLIENT)
	private float startPitch;

	@Environment(EnvType.CLIENT)
	private float startYaw;

	@Environment(EnvType.CLIENT)
	private boolean usingShader = false;

	@Environment(EnvType.CLIENT)
	private boolean started = false;

	public DefaultCutscene(int length)
	{
		this.length = length;
	}

	@Environment(EnvType.CLIENT)
	private void start()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		this.startPerspective = client.options.perspective;
		this.startPitch = client.player.pitch;
		this.startYaw = client.player.yaw;
		if (!worldType.isRealWorld()) this.cutsceneWorld = new CutsceneWorld(client, client.world, this.worldType == CutsceneWorldType.CLONE);
		if (this.initCallback != null) this.initCallback.accept(this);
		if (introTransition != null) introTransition.init();
		if (outroTransition != null) outroTransition.init();
		this.camera = new CutsceneCameraEntity(client.world).withPos(this.path.getPoint(0));
		this.started = true;
	}

	@Override
	public void tick()
	{
		if (ticks < length)
		{
			MinecraftClient client = MinecraftClient.getInstance();

			if (ticks == 0)
			{
				start();
			}

			if (shouldHideHud())
			{
				// Move Camera
				camera.update();
				camera.moveTo(path.getPoint(ticks / (float) length));

				if (!this.worldType.isRealWorld() && !(client.world instanceof CutsceneWorld))
				{
					client.player.setWorld(cutsceneWorld);
					client.world = cutsceneWorld;
					((MinecraftClientHooks) client).setCutsceneWorld(cutsceneWorld);
					ClientPlayNetworkHandler handler = client.getNetworkHandler();
					if (handler != null)
					{
						((ClientPlayNetworkHandlerHooks) handler).setCutsceneWorld(cutsceneWorld);
					}
					this.cutsceneWorld.addPlayer(client.player);
				}
				// Fix perspective
				if (client.options.perspective != 0)
				{
					client.options.perspective = 0;
					client.worldRenderer.method_3292();
				}

				// Set Camera
				if (client.cameraEntity != camera)
				{
					client.cameraEntity = camera;

					if (this.shader != null && !usingShader)
					{
						client.worldRenderer.method_3292();
						if (GLX.usePostProcess)
						{
							((GameRendererHooks) client.gameRenderer).useShader(this.shader);
						}
						usingShader = true;
					}
				}

				// Set Camera Look
				float percent = ticks / (float) length;

				Vector3f direction = path.getPoint(percent);
				direction.subtract(path.getPoint(percent >= 0.99f ? 0.9999f : percent + 0.01f));
				float lengthSquared = direction.x() * direction.x() + direction.y() * direction.y() + direction.z() * direction.z();
				if (lengthSquared != 0 && lengthSquared != 1) direction.scale(1 / (float) Math.sqrt(lengthSquared));

				camera.prevYaw = camera.yaw;
				camera.prevPitch = camera.pitch;

				camera.pitch = (float) Math.toDegrees(Math.asin(direction.y()));
				camera.yaw = (float) Math.toDegrees(Math.atan2(direction.x(), direction.z()));
			} else
			{
				// Restore real world
				if (!worldType.isRealWorld()) CutsceneManager.stopFakeWorld();

				// Disable Shader
				if (usingShader)
				{
					client.gameRenderer.disableShader();
					usingShader = false;
				}

				// Restore player camera
				if (client.getCameraEntity() == camera)
				{
					client.setCameraEntity(client.player);
					client.worldRenderer.method_3292();
				}

				// Restore perspective
				if (client.options.perspective != startPerspective)
				{
					client.options.perspective = startPerspective;
					client.worldRenderer.method_3292();
				}
			}

			// Update Transitions
			if (ticks < introTransition.length) introTransition.update();
			else if (ticks > length - outroTransition.length) outroTransition.update();

			// Callback
			if (tickCallback != null) tickCallback.accept(this);

			ticks++;

			if (ticks == length)
			{
				end();
			}
		}
	}

	@Override
	public void render()
	{
		MinecraftClient client = MinecraftClient.getInstance();

		// Render Transitions
		if (ticks < introTransition.length) introTransition.render(client, client.getTickDelta());
		else if (ticks > length - outroTransition.length) outroTransition.render(client, client.getTickDelta());

		// Callback
		if (renderCallback != null) renderCallback.accept(this);
	}

	@Override
	public void updateLook()
	{
		if (started)
		{
			MinecraftClient client = MinecraftClient.getInstance();
			client.player.pitch = startPitch;
			client.player.yaw = startYaw;
		}
	}

	@Environment(EnvType.CLIENT)
	private void end()
	{
		MinecraftClient client = MinecraftClient.getInstance();

		// Restore real world
		if (!worldType.isRealWorld()) CutsceneManager.stopFakeWorld();

		// Disable Shader
		if (usingShader)
		{
			client.gameRenderer.disableShader();
			usingShader = false;
		}

		// Restore perspective
		if (client.options.perspective != startPerspective)
		{
			client.options.perspective = startPerspective;
			client.worldRenderer.method_3292();
		}

		if (finishCallback != null) finishCallback.accept(this);

		CutsceneManager.finishClient();
	}

	@Override
	public void setCameraPath(Path path)
	{
		this.path = path.build();
	}

	@Override
	public void setShader(Identifier shader)
	{
		this.shader = shader;
	}

	@Override
	public void setIntroTransition(Transition introTransition)
	{
		this.introTransition = introTransition;
	}

	@Override
	public void setOutroTransition(Transition outroTransition)
	{
		this.outroTransition = outroTransition;
	}

	@Override
	public void setInitCallback(Consumer<Cutscene> initCallback)
	{
		this.initCallback = initCallback;
	}

	@Override
	public void setChunkGenCallback(Consumer<CutsceneChunk> chunkGenCallback)
	{
		this.chunkGenCallback = chunkGenCallback;
	}

	@Override
	public void setTickCallback(Consumer<Cutscene> tickCallback)
	{
		this.tickCallback = tickCallback;
	}

	@Override
	public void setRenderCallback(Consumer<Cutscene> renderCallback)
	{
		this.renderCallback = renderCallback;
	}

	@Override
	public void setFinishCallback(Consumer<Cutscene> finishCallback)
	{
		this.finishCallback = finishCallback;
	}

	@Override
	public void setWorldType(CutsceneWorldType worldType)
	{
		this.worldType = worldType;
	}

	@Override
	public Cutscene copy()
	{
		DefaultCutscene cutscene = new DefaultCutscene(length);

		cutscene.introTransition = this.introTransition;
		cutscene.outroTransition = this.outroTransition;
		cutscene.shader = this.shader;
		cutscene.initCallback = this.initCallback;
		cutscene.chunkGenCallback = this.chunkGenCallback;
		cutscene.tickCallback = this.tickCallback;
		cutscene.renderCallback = this.renderCallback;
		cutscene.finishCallback = this.finishCallback;
		cutscene.path = this.path;
		cutscene.worldType = this.worldType;

		return cutscene;
	}

	@Override
	public CutsceneWorld getWorld()
	{
		return cutsceneWorld;
	}

	@Override
	public Consumer<CutsceneChunk> getChunkGenCallback()
	{
		return chunkGenCallback;
	}

	@Override
	public int getTicks()
	{
		return ticks;
	}

	@Override
	public int getLength()
	{
		return length;
	}

	@Override
	public Path getCameraPath()
	{
		return path;
	}

	@Override
	public boolean shouldHideHud()
	{
		if (ticks < introTransition.length && introTransition.isFirstHalf()) return false;
		else if (ticks > length - outroTransition.length && !outroTransition.isFirstHalf()) return false;
		return true;
	}
}
