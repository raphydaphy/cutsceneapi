package com.raphydaphy.cutsceneapi.cutscene;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

public abstract class Transition
{
	float startTime;
	float length;
	boolean showHud = false;
	boolean fixedCamera = false;

	public Transition(float startTime, float length)
	{
		this.startTime = startTime;
		this.length = length;
	}

	public abstract void render(MinecraftClient client, float cutsceneTime);

	public boolean active(float cutsceneTime)
	{
		return cutsceneTime >= startTime && cutsceneTime <= startTime + length;
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

		public FadeTo(float startTime, float length, int red, int green, int blue)
		{
			super(startTime, length);
			this.red = red / 255f;
			this.green = green / 255f;
			this.blue = blue / 255f;
		}

		@Override
		public void render(MinecraftClient client, float cutsceneTime)
		{
			float transitionTime = cutsceneTime - startTime;
			float percent = 1 - transitionTime / length;
			drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
		}
	}

	public static class FadeFrom extends FadeTo
	{
		public FadeFrom(float startTime, float length, int red, int green, int blue)
		{
			super(startTime, length, red, green, blue);
		}

		@Override
		public void render(MinecraftClient client, float cutsceneTime)
		{
			float transitionTime = cutsceneTime - startTime;
			float percent = transitionTime / length;
			drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
		}
	}

	public static class DipTo extends FadeTo
	{
		private boolean isIntro = false;
		private boolean isOutro = false;
		private float hold;

		public DipTo(float startTime, float length, float hold, int red, int green, int blue)
		{
			super(startTime, length + hold, red, green, blue);
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
		public boolean active(float cutsceneTime)
		{
			if (isIntro)
			{
				return cutsceneTime <= startTime + length;
			} else if (isOutro)
			{
				return cutsceneTime >= startTime;
			}
			return cutsceneTime >= startTime && cutsceneTime <= startTime + length;
		}

		@Override
		public void render(MinecraftClient client, float cutsceneTime)
		{
			float transitionTime = cutsceneTime - startTime;
			float halfTime = (length - hold) / 2f;
			GlStateManager.disableDepthTest();
			if (transitionTime < halfTime)
			{
				showHud = isIntro;
				fixedCamera = isIntro;
				float percent = transitionTime / halfTime;
				drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
			} else if (transitionTime < halfTime + hold)
			{
				showHud = isOutro;
				fixedCamera = isIntro;
				drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), 1, red, green, blue);
			} else
			{
				transitionTime = transitionTime - halfTime - hold;
				showHud = isOutro;
				fixedCamera = isOutro;
				float percent = 1 - transitionTime / halfTime;
				drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
			}
			GlStateManager.enableDepthTest();
		}
	}

	private static void drawRect(int x, int y, int width, int height, float alpha, float red, float green, float blue)
	{
		int int_7;
		if (x < width)
		{
			int_7 = x;
			x = width;
			width = int_7;
		}

		if (y < height)
		{
			int_7 = y;
			y = height;
			height = int_7;
		}

		Tessellator tessellator_1 = Tessellator.getInstance();
		BufferBuilder bufferBuilder_1 = tessellator_1.getBufferBuilder();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color4f(red, green, blue, alpha);
		bufferBuilder_1.begin(7, VertexFormats.POSITION);
		bufferBuilder_1.vertex((double) x, (double) height, 0.0D).next();
		bufferBuilder_1.vertex((double) width, (double) height, 0.0D).next();
		bufferBuilder_1.vertex((double) width, (double) y, 0.0D).next();
		bufferBuilder_1.vertex((double) x, (double) y, 0.0D).next();
		tessellator_1.draw();
		GlStateManager.enableTexture();
		GlStateManager.disableBlend();
	}
}
