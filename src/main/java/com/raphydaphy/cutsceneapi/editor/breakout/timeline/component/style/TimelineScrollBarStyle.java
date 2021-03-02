package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style;

import com.raphydaphy.shaded.org.joml.Vector4f;
import org.liquidengine.legui.style.color.ColorUtil;

public class TimelineScrollBarStyle {
  private Vector4f barColor = ColorUtil.fromInt(105, 105, 105, 1);
  private Vector4f handleColor = ColorUtil.fromInt(168, 168, 168, 1);
  private Vector4f handleHoverColor = ColorUtil.fromInt(204, 204, 204, 1);

  public Vector4f getBarColor() {
    return barColor;
  }

  public TimelineScrollBarStyle setBarColor(Vector4f barColor) {
    this.barColor = barColor;
    return this;
  }

  public Vector4f getHandleColor() {
    return handleColor;
  }

  public TimelineScrollBarStyle setHandleColor(Vector4f handleColor) {
    this.handleColor = handleColor;
    return this;
  }

  public Vector4f getHandleHoverColor() {
    return handleHoverColor;
  }

  public TimelineScrollBarStyle setHandleHoverColor(Vector4f handleHoverColor) {
    this.handleHoverColor = handleHoverColor;
    return this;
  }
}
