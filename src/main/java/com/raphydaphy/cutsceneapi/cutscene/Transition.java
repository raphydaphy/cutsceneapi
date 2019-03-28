package com.raphydaphy.cutsceneapi.cutscene;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raphydaphy.cutsceneapi.utils.CutsceneUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

@Environment(EnvType.CLIENT)
public abstract class Transition
{
	int ticks;
	float length;
	boolean showHud = false;
	boolean fixedCamera = false;

	public Transition(float length)
	{
		this.length = length;
	}

	public abstract void render(MinecraftClient client, float tickDelta);

	public void update()
	{
		ticks++;
	}

	public boolean showHud()
	{
		return showHud;
	}

	public boolean fixedCamera()
	{
		return fixedCamera;
	}

	public static class FadeTo extends Transition
	{
		protected float red;
		protected float green;
		protected float blue;

		public FadeTo(float length, int red, int green, int blue)
		{
			super(length);
			this.red = red / 255f;
			this.green = green / 255f;
			this.blue = blue / 255f;
		}

		@Override
		public void render(MinecraftClient client, float tickDelta)
		{
			float transitionTime = CutsceneUtils.lerp(ticks - 1, ticks, tickDelta);
			float percent = 1 - transitionTime / length;
			CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
		}
	}

	public static class FadeFrom extends FadeTo
	{
		public FadeFrom(float length, int red, int green, int blue)
		{
			super(length, red, green, blue);
		}

		@Override
		public void render(MinecraftClient client, float tickDelta)
		{
			float transitionTime = CutsceneUtils.lerp(ticks - 1, ticks, tickDelta);
			float percent = transitionTime / length;
			CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
		}
	}

	public static class DipTo extends FadeTo
	{
		private boolean isIntro = false;
		private boolean isOutro = false;
		private float hold;

		public DipTo(float length, float hold, int red, int green, int blue)
		{
			super(length + hold, red, green, blue);
			this.hold = hold;
		}

		public DipTo setIntro()
		{
			this.isIntro = true;
			return this;
		}

		public DipTo setOutro()
		{
			this.isOutro = true;
			return this;
		}

		@Override
		public void render(MinecraftClient client, float tickDelta)
		{
			float transitionTime = CutsceneUtils.lerp(ticks - 1, ticks, tickDelta);
			float halfTime = (length - hold) / 2f;
			GlStateManager.disableDepthTest();
			if (transitionTime < halfTime)
			{
				showHud = isIntro;
				fixedCamera = isIntro;
				float percent = transitionTime / halfTime;
				CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
			} else if (transitionTime < halfTime + hold)
			{
				showHud = isOutro;
				fixedCamera = isIntro;
				CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), 1, red, green, blue);
			} else
			{
				transitionTime = transitionTime - halfTime - hold;
				showHud = isOutro;
				fixedCamera = isOutro;
				float percent = 1 - transitionTime / halfTime;
				CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
			}
			GlStateManager.enableDepthTest();
		}
	}
}
