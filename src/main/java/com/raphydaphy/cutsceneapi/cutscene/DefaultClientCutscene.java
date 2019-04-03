package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.api.ClientCutscene;
import com.raphydaphy.cutsceneapi.api.CutsceneAPI;
import com.raphydaphy.cutsceneapi.api.Path;
import com.raphydaphy.cutsceneapi.camera.CutsceneCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class DefaultClientCutscene extends DefaultCutscene implements ClientCutscene
{
	// Settings
	private Consumer<ClientCutscene> clientInitCallback;
	private Consumer<ClientCutscene> clientTickCallback;
	private Consumer<ClientCutscene> renderCallback;
	private Consumer<ClientCutscene> clientEndCallback;
	private Path path;

	// Data
	private int clientTicks;
	private CutsceneCamera camera;

	@Override
	public void setClientPath(Path path)
	{
		this.path = path;
	}

	@Override
	public void setClientInitCallback(Consumer<ClientCutscene> callback)
	{
		this.clientInitCallback = callback;
	}

	@Override
	public void setClientTickCallback(Consumer<ClientCutscene> callback)
	{
		this.clientTickCallback = callback;
	}

	@Override
	public void setRenderCallback(Consumer<ClientCutscene> callback)
	{
		this.renderCallback = callback;
	}

	@Override
	public void setClientEndCallback(Consumer<ClientCutscene> callback)
	{
		this.clientEndCallback = callback;
	}

	@SideOnly(Side.CLIENT)
	private void start()
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		camera = new CutsceneCamera(minecraft.world);
		if (clientInitCallback != null) clientInitCallback.accept(this);
	}

	@Override
	public void clientTick()
	{
		if (clientTicks < length)
		{
			if (clientTicks == 0)
			{
				start();
			}

			if (clientTickCallback != null) clientTickCallback.accept(this);

			clientTicks++;

			if (clientTicks == length)
			{
				end();
			}
		}
	}

	@Override
	public void render()
	{
		if (renderCallback != null) renderCallback.accept(this);
	}

	@SideOnly(Side.CLIENT)
	private void end()
	{
		Minecraft client = Minecraft.getMinecraft();
		CutsceneAPI.getCutsceneManager().stop(client.player);
		if (clientEndCallback != null) clientEndCallback.accept(this);
	}

	@Override
	public ResourceLocation getID()
	{
		return id;
	}

	@Override
	public int getLength()
	{
		return length;
	}

	@Override
	public DefaultClientCutscene copy()
	{
		DefaultClientCutscene cutscene = new DefaultClientCutscene();
		applySettings(cutscene);
		cutscene.setClientPath(path);
		cutscene.setClientInitCallback(clientInitCallback);
		cutscene.setClientTickCallback(clientTickCallback);
		cutscene.setRenderCallback(renderCallback);
		cutscene.setClientEndCallback(clientEndCallback);
		return cutscene;
	}
}
