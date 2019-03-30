package com.raphydaphy.cutsceneapi.cutscene;

import com.mojang.blaze3d.platform.GLX;
import com.raphydaphy.cutsceneapi.api.ClientCutscene;
import com.raphydaphy.cutsceneapi.api.Cutscene;
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
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class DefaultClientCutscene extends DefaultCutscene implements ClientCutscene
{
	// Settings
	private Transition introTransition;
	private Transition outroTransition;
	private Identifier shader;
	private Consumer<CutsceneChunk> chunkGenCallback;
	private Consumer<ClientCutscene> renderCallback;
	private Path path;
	private CutsceneWorldType worldType = CutsceneWorldType.REAL;
	private ClientCutscene nextCutscene;

	// Client Data
	private CutsceneCameraEntity camera;
	private CutsceneWorld cutsceneWorld;
	private int startPerspective;
	private float startPitch;
	private float startYaw;
	private boolean usingShader = false;

	public DefaultClientCutscene(int length)
	{
		super(length);
	}

	private void start()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		this.startPerspective = client.options.perspective;
		this.startPitch = client.player.pitch;
		this.startYaw = client.player.yaw;
		if (!worldType.isRealWorld())
		{
			if (worldType == CutsceneWorldType.PREVIOUS)
			{
				ClientWorld world = client.world;
				if (world instanceof CutsceneWorld)
				{
					this.cutsceneWorld = (CutsceneWorld)world;
				}
			} else
			{
				this.cutsceneWorld = new CutsceneWorld(client, client.world, this.worldType == CutsceneWorldType.CLONE);
			}
		}
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
			if (introTransition != null && ticks < introTransition.length) introTransition.update();
			else if (outroTransition != null && ticks > length - outroTransition.length) outroTransition.update();

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
		if (introTransition != null && ticks < introTransition.length) introTransition.render(client, client.getTickDelta());
		else if (outroTransition != null && ticks > length - outroTransition.length) outroTransition.render(client, client.getTickDelta());

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
	public void setRenderCallback(Consumer<ClientCutscene> renderCallback)
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
	public void setNextCutscene(ClientCutscene nextCutscene)
	{
		this.nextCutscene = nextCutscene;
	}

	@Override
	public Cutscene copy()
	{
		DefaultClientCutscene cutscene = new DefaultClientCutscene(length);

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
		cutscene.nextCutscene = this.nextCutscene;

		return cutscene;
	}

	@Override
	public CutsceneWorld getWorld()
	{
		return cutsceneWorld;
	}

	@Override
	public ClientCutscene getNextCutscene()
	{
		return nextCutscene;
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
		if (introTransition != null && ticks < introTransition.length && introTransition.isFirstHalf()) return false;
		else if (outroTransition != null && ticks > length - outroTransition.length && !outroTransition.isFirstHalf()) return false;
		return true;
	}
}
