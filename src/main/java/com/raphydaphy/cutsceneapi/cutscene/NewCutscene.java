package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import com.raphydaphy.cutsceneapi.mixin.client.ClientPlayNetworkHandlerHooks;
import com.raphydaphy.cutsceneapi.mixin.client.MinecraftClientHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import sun.tools.java.Identifier;

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
	private Consumer<ICutscene> introCallback;

	@Environment(EnvType.CLIENT)
	private Consumer<ICutscene> tickCallback;

	@Environment(EnvType.CLIENT)
	private Consumer<ICutscene> outroCallback;

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
	void start()
	{
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
		if (this.introCallback != null) this.introCallback.accept(this);
		this.camera = new CutsceneCameraEntity(client.world).withPos(this.path.getPoint(0));
	}

	@Environment(EnvType.CLIENT)
	void tick()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		ticks++;
	}

	@Environment(EnvType.CLIENT)
	void end()
	{

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
	public void setIntroCallback(Consumer<ICutscene> introCallback)
	{
		this.introCallback = introCallback;
	}

	@Override
	public void setTickCallback(Consumer<ICutscene> tickCallback)
	{
		this.tickCallback = tickCallback;
	}

	@Override
	public void setOutroCallback(Consumer<ICutscene> outroCallback)
	{
		this.outroCallback = outroCallback;
	}

	@Override
	public void setWorldType(CutsceneWorldType worldType)
	{
		this.worldType = worldType;
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
