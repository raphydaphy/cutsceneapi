package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style;

import com.raphydaphy.shaded.org.joml.Vector4f;
import org.liquidengine.legui.style.color.ColorUtil;

public class TimelineStyle {

  private float topHeight = 30;
  private float headSize = 10;

  private Vector4f markerLabelColor = ColorUtil.fromInt(64, 64, 64, 1f);
  private Vector4f markerColor = new Vector4f(markerLabelColor);

  private Vector4f baselineColor = new Vector4f(markerLabelColor);
  private float baselineSize = 2;

  private Vector4f hoveredMarkerColor = ColorUtil.fromInt(120, 120, 120, 1f);
  private Vector4f headColor = ColorUtil.fromInt(94, 162, 230, 1f);

  public float getTopHeight() {
    return topHeight;
  }

  public void setTopHeight(float topHeight) {
    this.topHeight = topHeight;
  }

  public float getHeadSize() {
    return headSize;
  }

  public void setHeadSize(float headSize) {
    this.headSize = headSize;
  }

  public Vector4f getMarkerLabelColor() {
    return markerLabelColor;
  }

  public void setMarkerLabelColor(Vector4f markerLabelColor) {
    this.markerLabelColor = markerLabelColor;
  }

  public Vector4f getMarkerColor() {
    return markerColor;
  }

  public void setMarkerColor(Vector4f markerColor) {
    this.markerColor = markerColor;
  }

  public Vector4f getBaselineColor() {
    return baselineColor;
  }

  public void setBaselineColor(Vector4f baselineColor) {
    this.baselineColor = baselineColor;
  }

  public float getBaselineSize() {
    return baselineSize;
  }

  public void setBaselineSize(float baselineSize) {
    this.baselineSize = baselineSize;
  }

  public Vector4f getHoveredMarkerColor() {
    return hoveredMarkerColor;
  }

  public void setHoveredMarkerColor(Vector4f hoveredMarkerColor) {
    this.hoveredMarkerColor = hoveredMarkerColor;
  }

  public Vector4f getHeadColor() {
    return headColor;
  }

  public void setHeadColor(Vector4f headColor) {
    this.headColor = headColor;
  }

}
