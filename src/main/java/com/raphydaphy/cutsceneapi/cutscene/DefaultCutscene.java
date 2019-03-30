package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.api.Path;
import com.raphydaphy.cutsceneapi.camera.CutsceneCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Consumer;

public class DefaultCutscene implements Cutscene
{
	// Common Settings
	private ResourceLocation id;
	private int length;
	private Consumer<Cutscene> initCallback;
	private Consumer<Cutscene> tickCallback;
	private Consumer<Cutscene> endCallback;

	// Client Settings
	@SideOnly(Side.CLIENT)
	private Path path;
	@SideOnly(Side.CLIENT)
	private Consumer<Cutscene> renderCallback;

	// Data
	private int ticks;
	@SideOnly(Side.CLIENT)
	private CutsceneCamera camera;

	@Override
	public void setID(ResourceLocation id)
	{
		this.id = id;
	}

	@Override
	public void setLength(int length)
	{
		this.length = length;
	}

	@Override
	public void setPath(Path path)
	{
		this.path = path;
	}

	@Override
	public void setInitCallback(Consumer<Cutscene> callback)
	{
		this.initCallback = callback;
	}

	@Override
	public void setTickCallback(Consumer<Cutscene> callback)
	{
		this.tickCallback = callback;
	}

	@Override
	public void setRenderCallback(Consumer<Cutscene> callback)
	{
		this.renderCallback = callback;
	}

	@Override
	public void setEndCallback(Consumer<Cutscene> callback)
	{
		this.endCallback = callback;
	}

	@SideOnly(Side.CLIENT)
	private void start()
	{
		Minecraft minecraft = Minecraft.getMinecraft();
		camera = new CutsceneCamera(minecraft.world);
		if (initCallback != null) initCallback.accept(this);
	}

	@Override
	public void tick()
	{
		if (ticks < length)
		{
			if (ticks == 0)
			{
				start();
			}

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
		if (renderCallback != null) renderCallback.accept(this);
	}

	@SideOnly(Side.CLIENT)
	private void end()
	{
		if (endCallback != null) endCallback.accept(this);
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
	public DefaultCutscene copy(boolean client)
	{
		DefaultCutscene cutscene = new DefaultCutscene();
		cutscene.setID(id);
		cutscene.setLength(length);
		cutscene.setInitCallback(initCallback);
		cutscene.setTickCallback(tickCallback);
		cutscene.setEndCallback(endCallback);

		if (client)
		{
			requestClientCopy(cutscene);
		}
		return cutscene;
	}

	private void requestClientCopy(Cutscene cutscene)
	{
		copyClientSettings(cutscene);
	}

	@SideOnly(Side.CLIENT)
	private void copyClientSettings(Cutscene cutscene)
	{
		cutscene.setPath(path);
		cutscene.setRenderCallback(renderCallback);
	}
}
