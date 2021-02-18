package com.raphydaphy.cutsceneapi.editor;

import org.lwjgl.glfw.GLFW;

public class CutsceneEditorWindow implements AutoCloseable {
  private final long handle;
  private int width = 300;
  private int height = 720;
  private double scaleFactor = 1D;

  public CutsceneEditorWindow() {
    this.handle = GLFW.glfwCreateWindow(this.width, this.height, "Cutscene Editor", 0L, 0L);

    GLFW.glfwMakeContextCurrent(this.handle);
    GLFW.glfwSetWindowSizeLimits(this.handle, 100, 300, 300, GLFW.GLFW_DONT_CARE);
    GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeChanged);
  }

  private void onWindowSizeChanged(long window, int width, int height) {
    this.width = width;
    this.height = height;
  }

  public long getHandle() {
    return this.handle;
  }

  public int getWidth() {
    return this.width;
  }

  public int getHeight() {
    return this.height;
  }

  public double getScaleFactor() {
    return this.scaleFactor;
  }

  public boolean shouldClose() {
    return GLFW.glfwWindowShouldClose(handle);
  }

  @Override
  public void close() {
    GLFW.glfwDestroyWindow(this.handle);
  }
}
