package com.raphydaphy.cutsceneapi.cutscene;

import com.mojang.blaze3d.platform.GLX;
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

public class NewCutscene implements ICutscene
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
	private Consumer<ICutscene> initCallback;

	@Environment(EnvType.CLIENT)
	private Consumer<ICutscene> tickCallback;

	@Environment(EnvType.CLIENT)
	private Consumer<ICutscene> renderCallback;

	@Environment(EnvType.CLIENT)
	private Consumer<ICutscene> finishCallback;

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

	public NewCutscene(int length)
	{
		this.length = length;
	}

	@Environment(EnvType.CLIENT)
	private void start()
	{
		if (this.initCallback != null) this.initCallback.accept(this);

		MinecraftClient client = MinecraftClient.getInstance();
		this.startPerspective = client.options.perspective;
		this.startPitch = client.player.pitch;
		this.startYaw = client.player.yaw;
		if (!this.worldType.isRealWorld())
		{
			this.cutsceneWorld = new CutsceneWorld(client, client.world, this.worldType == CutsceneWorldType.CLONE);
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
		this.camera = new CutsceneCameraEntity(client.world).withPos(this.path.getPoint(0));
	}

	@Override
	public void tick()
	{
		MinecraftClient client = MinecraftClient.getInstance();

		if (ticks == 0)
		{
			start();
		}

		// Move Camera
		camera.update();
		camera.moveTo(path.getPoint(ticks / (float) length));

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

			if (this.shader != null)
			{
				client.worldRenderer.method_3292();
				if (GLX.usePostProcess)
				{
					((GameRendererHooks) client.gameRenderer).useShader(this.shader);
				}
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

		// Update Transitions
		if (ticks < introTransition.length) introTransition.update();
		else if (length - ticks < outroTransition.length) outroTransition.update();

		// Callback
		if (tickCallback != null) tickCallback.accept(this);

		ticks++;

		if (ticks == length)
		{
			end();
		}
	}

	@Override
	public void render()
	{
		MinecraftClient client = MinecraftClient.getInstance();

		// Render Transitions
		if (ticks < introTransition.length) introTransition.render(client, client.getTickDelta());
		else if (length - ticks < outroTransition.length) outroTransition.render(client, client.getTickDelta());

		// Callback
		if (renderCallback != null) renderCallback.accept(this);
	}

	@Environment(EnvType.CLIENT)
	private void end()
	{
		MinecraftClient client = MinecraftClient.getInstance();

		// Restore real world
		if (!worldType.isRealWorld()) CutsceneManager.stopFakeWorld();

		// Disable Shader
		if (shader != null) client.gameRenderer.disableShader();

		// Restore player camera
		client.setCameraEntity(client.player);
		client.worldRenderer.method_3292();

		// Fix perspective
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
		this.path = path;
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
	public void setInitCallback(Consumer<ICutscene> initCallback)
	{
		this.initCallback = initCallback;
	}

	@Override
	public void setTickCallback(Consumer<ICutscene> tickCallback)
	{
		this.tickCallback = tickCallback;
	}

	@Override
	public void setRenderCallback(Consumer<ICutscene> renderCallback)
	{
		this.renderCallback = renderCallback;
	}

	@Override
	public void setFinishCallback(Consumer<ICutscene> finishCallback)
	{
		this.finishCallback = finishCallback;
	}

	@Override
	public void setWorldType(CutsceneWorldType worldType)
	{
		this.worldType = worldType;
	}

	@Override
	public ICutscene copy()
	{
		NewCutscene cutscene = new NewCutscene(length);

		cutscene.introTransition = this.introTransition;
		cutscene.outroTransition = this.outroTransition;
		cutscene.shader = this.shader;
		cutscene.initCallback = this.initCallback;
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
}
