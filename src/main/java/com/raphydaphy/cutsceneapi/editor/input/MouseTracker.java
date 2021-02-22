package com.raphydaphy.cutsceneapi.editor.input;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class MouseTracker {
  private double x, y, prevX, prevY, horizontalScrollDelta, verticalScrollDelta;
  boolean leftButtonDown, middleButtonDown, rightButtonDown;

  public MouseTracker(double x, double y) {
    this.x = x;
    this.y = y;

    this.prevX = x;
    this.prevY = y;
  }

  public void update() {
    this.prevX = this.x;
    this.prevY = this.y;

    this.horizontalScrollDelta = 0;
    this.verticalScrollDelta = 0;
  }

  public void onMouseButton(long window, int button, int action, int mods) {
    if (window != MinecraftClient.getInstance().getWindow().getHandle()) return;
    if (action == GLFW.GLFW_PRESS) {
      if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
        this.leftButtonDown = true;
      } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
        this.middleButtonDown = true;
      } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
        this.rightButtonDown = true;
      }
    } else if (action == GLFW.GLFW_RELEASE) {
      if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
        this.leftButtonDown = false;
      } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
        this.middleButtonDown = false;
      }else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
        this.rightButtonDown = false;
      }
    }

  }

  public void onCursorPos(long window, double x, double y) {
    if (window != MinecraftClient.getInstance().getWindow().getHandle()) return;

    this.x = x;
    this.y = y;
  }

  public void onMouseScroll(long window, double horizontal, double vertical) {
    if (window != MinecraftClient.getInstance().getWindow().getHandle()) return;

    this.horizontalScrollDelta += horizontal;
    this.verticalScrollDelta += vertical;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  public double getPrevX() {
    return this.prevX;
  }

  public double getPrevY() {
    return this.prevY;
  }

  public double getCursorDeltaX() {
    return this.x - this.prevX;
  }

  public double getCursorDeltaY() {
    return this.y - this.prevY;
  }

  public boolean isLeftButtonDown() {
    return this.leftButtonDown;
  }

  public boolean isRightButtonDown() {
    return this.rightButtonDown;
  }

  public boolean isMiddleButtonDown() {
    return this.middleButtonDown;
  }

  public double getHorizontalScrollDelta() {
    return this.horizontalScrollDelta;
  }

  public double getVerticalScrollDelta() {
    return this.verticalScrollDelta;
  }
}
