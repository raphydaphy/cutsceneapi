package com.raphydaphy.cutsceneapi.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;

public class CutsceneUtils {

    public static float lerp(float previous, float current, float delta) {
        return (1 - delta) * previous + delta * current;
    }

    @Environment(EnvType.CLIENT)
    public static void drawRect(int x, int y, int width, int height, float alpha, float red, float green, float blue) {
        int int_7;
        if (x < width) {
            int_7 = x;
            x = width;
            width = int_7;
        }

        if (y < height) {
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
