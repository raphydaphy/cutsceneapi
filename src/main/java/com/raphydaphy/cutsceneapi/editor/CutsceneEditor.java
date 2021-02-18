package com.raphydaphy.cutsceneapi.editor;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL30;

public class CutsceneEditor {
  private CutsceneEditorWindow window;


  public CutsceneEditor() {
    this.window = new CutsceneEditorWindow();
  }

  private static Identifier LEAVES_TEXTURE = new Identifier("textures/block/azalea_leaves.png");

  public void render() {
    if (window.shouldClose()) return;

    GLFW.glfwMakeContextCurrent(window.getHandle());

    RenderSystem.pushMatrix();

    RenderSystem.enableTexture();
    RenderSystem.clear(GL30.GL_COLOR_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

    GlStateManager.clearColor(1, 1, 0, 1);

    /*
    float width = (float) window.getWidth();
    float height = (float) window.getHeight();
    float scale = (float) window.getScaleFactor();

    RenderSystem.loadIdentity();
    RenderSystem.ortho(0.0D, (double)width / scale, (double)height / scale, 0.0D, 1000.0D, 3000.0D);
    */

    /*
    MinecraftClient client = MinecraftClient.getInstance();
    MatrixStack stack = new MatrixStack();

    client.getTextureManager().bindTexture(LEAVES_TEXTURE);
    DrawableHelper.drawTexture(stack, 0, 0, 0, 0, 0, 180, 180, 16, 16);
    */

    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1F);

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder bufferBuilder = tessellator.getBuffer();

    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
    bufferBuilder.vertex(100, 0, 0).color(1, 1, 1, 1).next();
    bufferBuilder.vertex(100, 100, 0).color(1, 0, 1, 1).next();
    bufferBuilder.vertex(0, 100, 0).color(1, 1, 0, 1).next();
    bufferBuilder.vertex(0, 0, 0).color(0, 1, 1, 1).next();
    tessellator.draw();

    GLFW.glfwSwapBuffers(window.getHandle());

    RenderSystem.popMatrix();
  }
}
