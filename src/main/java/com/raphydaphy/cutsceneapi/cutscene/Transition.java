package com.raphydaphy.cutsceneapi.cutscene;

import com.mojang.blaze3d.platform.GlStateManager;
import com.raphydaphy.cutsceneapi.utils.CutsceneUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public abstract class Transition {
    int ticks;
    float length;

    public Transition(float length) {
        this.length = length;
    }

    public void init() {
        this.ticks = 0;
    }

    public abstract void render(MinecraftClient client, float tickDelta);

    public void update() {
        ticks++;
    }

    public abstract boolean isFirstHalf();

    public static class FadeTo extends Transition {
        protected float red;
        protected float green;
        protected float blue;

        public FadeTo(float length, int red, int green, int blue) {
            super(length);
            this.red = red / 255f;
            this.green = green / 255f;
            this.blue = blue / 255f;
        }

        @Override
        public void render(MinecraftClient client, float tickDelta) {
            float transitionTime = CutsceneUtils.lerp(ticks - 1, ticks, tickDelta);
            float percent = transitionTime / length;
            CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
        }

        @Override
        public boolean isFirstHalf() {
            return true;
        }
    }

    public static class FadeFrom extends FadeTo {
        public FadeFrom(float length, int red, int green, int blue) {
            super(length, red, green, blue);
        }

        @Override
        public void render(MinecraftClient client, float tickDelta) {
            float transitionTime = CutsceneUtils.lerp(ticks - 1, ticks, tickDelta);
            float percent = 1 - transitionTime / length;
            CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
        }

        @Override
        public boolean isFirstHalf() {
            return false;
        }
    }

    public static class DipTo extends FadeTo {
        private float hold;

        public DipTo(float length, float hold, int red, int green, int blue) {
            super(length + hold, red, green, blue);
            this.hold = hold;
        }

        @Override
        public void render(MinecraftClient client, float tickDelta) {
            float transitionTime = CutsceneUtils.lerp(ticks - 1, ticks, tickDelta);
            float halfTime = (length - hold) / 2f;
            GlStateManager.disableDepthTest();
            if (transitionTime < halfTime) {
                float percent = transitionTime / halfTime;
                CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
            } else if (transitionTime < halfTime + hold) {
                CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), 1, red, green, blue);
            } else {
                transitionTime = transitionTime - halfTime - hold;
                float percent = 1 - transitionTime / halfTime;
                CutsceneUtils.drawRect(0, 0, client.window.getScaledWidth(), client.window.getScaledHeight(), percent, red, green, blue);
            }
            GlStateManager.enableDepthTest();
        }

        public boolean isFirstHalf() {
            return ticks - 1 < (length - hold) / 2f;
        }
    }
}
