package com.raphydaphy.cutsceneapi.cutscene;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raphydaphy.cutsceneapi.utils.CutsceneUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public abstract class Transition
{
	int ticks;
	float length;
	boolean firstHalf = true;

	public Transition(float length)
	{
		this.length = length;
	}

	public void init()
	{
		this.ticks = 0;
		this.firstHalf = true;
	}

	public abstract void render(MinecraftClient client, float tickDelta);

	public void update()
	{
		ticks++;
	}

	public boolean isFirstHalf()
	{
		return firstHalf;
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
		public void init()
		{
			this.firstHalf = false;
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
		private float hold;

		public DipTo(float length, float hold, int red, int green, int blue)
		{
			super(length + hold, red, green, blue);
			this.hold = hold;
		}

		@Override
		public void render(MinecraftClient client, float tickDelta)
		{
			float transitionTime = CutsceneUtils.lerp(ticks - 1, ticks, tickDelta);
			float halfTime = (length - hold) / 2f;
			GlStateManager.disableDepthTest();
			if (transitionTime < halfTime)
			{
				firstHalf = true;
				float percent = transitionTime / halfTime;
				CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
			} else if (transitionTime < halfTime + hold)
			{
				firstHalf = false;
				CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), 1, red, green, blue);
			} else
			{
				transitionTime = transitionTime - halfTime - hold;
				firstHalf = false;
				float percent = 1 - transitionTime / halfTime;
				CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
			}
			GlStateManager.enableDepthTest();
		}
	}
}
