package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style;

import com.raphydaphy.shaded.org.joml.Vector2f;
import com.raphydaphy.shaded.org.joml.Vector4f;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.color.ColorUtil;

public class TimelineStyle {

  private float topHeight = 50;
  private float headSize = 10;

  private Vector4f markerLabelColor = ColorUtil.fromInt(64, 64, 64, 1f);
  private Vector4f markerColor = new Vector4f(markerLabelColor);

  private Vector4f baselineColor = new Vector4f(markerColor);
  private float baselineSize = 2;

  private Vector4f hoveredMarkerColor = ColorUtil.fromInt(120, 120, 120, 1f);
  private Vector4f headColor = ColorUtil.fromInt(94, 162, 230, 1f);

  private float trackHeight = 60;
  private float trackSeparatorSize = 1;
  private Vector4f trackSeparatorColor = new Vector4f(markerColor);

  private Vector2f controlButtonSize = new Vector2f(30f, 20f);
  private Vector2f controlButtonIconSize = new Vector2f(controlButtonSize.y).mul(2 / 3f);

  private Vector4f controlButtonIconColor = ColorUtil.fromInt(80, 80, 80, 1f);
  private Vector4f hoveredControlButtonIconColor = new Vector4f(markerColor);

  private Vector4f controlButtonBackgroundColor = ColorConstants.transparent();
  private Vector4f hoveredControlButtonBackgroundColor = ColorUtil.fromInt(214, 214, 214, 1f);
  private Vector4f pressedControlButtonBackgroundColor = ColorUtil.fromInt(184, 184, 184, 1f);

  public float getTopHeight() {
    return this.topHeight;
  }

  public TimelineStyle setTopHeight(float topHeight) {
    this.topHeight = topHeight;
    return this;
  }

  public float getHeadSize() {
    return this.headSize;
  }

  public TimelineStyle setHeadSize(float headSize) {
    this.headSize = headSize;
    return this;
  }

  public Vector4f getMarkerLabelColor() {
    return this.markerLabelColor;
  }

  public TimelineStyle setMarkerLabelColor(Vector4f markerLabelColor) {
    this.markerLabelColor = markerLabelColor;
    return this;
  }

  public Vector4f getMarkerColor() {
    return this.markerColor;
  }

  public TimelineStyle setMarkerColor(Vector4f markerColor) {
    this.markerColor = markerColor;
    return this;
  }

  public Vector4f getBaselineColor() {
    return this.baselineColor;
  }

  public TimelineStyle setBaselineColor(Vector4f baselineColor) {
    this.baselineColor = baselineColor;
    return this;
  }

  public float getBaselineSize() {
    return this.baselineSize;
  }

  public TimelineStyle setBaselineSize(float baselineSize) {
    this.baselineSize = baselineSize;
    return this;
  }

  public Vector4f getHoveredMarkerColor() {
    return this.hoveredMarkerColor;
  }

  public TimelineStyle setHoveredMarkerColor(Vector4f hoveredMarkerColor) {
    this.hoveredMarkerColor = hoveredMarkerColor;
    return this;
  }

  public Vector4f getHeadColor() {
    return this.headColor;
  }

  public TimelineStyle setHeadColor(Vector4f headColor) {
    this.headColor = headColor;
    return this;
  }


  public float getTrackHeight() {
    return this.trackHeight;
  }

  public TimelineStyle setTrackHeight(float trackHeight) {
    this.trackHeight = trackHeight;
    return this;
  }

  public float getTrackSeparatorSize() {
    return this.trackSeparatorSize;
  }

  public TimelineStyle setTrackSeparatorSize(float trackSeparatorSize) {
    this.trackSeparatorSize = trackSeparatorSize;
    return this;
  }

  public Vector4f getTrackSeparatorColor() {
    return this.trackSeparatorColor;
  }

  public TimelineStyle setTrackSeparatorColor(Vector4f trackSeparatorColor) {
    this.trackSeparatorColor = trackSeparatorColor;
    return this;
  }

  public Vector2f getControlButtonSize() {
    return this.controlButtonSize;
  }

  public TimelineStyle setControlButtonSize(Vector2f controlButtonSize) {
    this.controlButtonSize = controlButtonSize;
    return this;
  }

  public Vector2f getControlButtonIconSize() {
    return this.controlButtonIconSize;
  }

  public TimelineStyle setControlButtonIconSize(Vector2f controlButtonIconSize) {
    this.controlButtonIconSize = controlButtonIconSize;
    return this;
  }

  public Vector4f getControlButtonIconColor() {
    return this.controlButtonIconColor;
  }

  public TimelineStyle setControlButtonIconColor(Vector4f controlButtonIconColor) {
    this.controlButtonIconColor = controlButtonIconColor;
    return this;
  }

  public Vector4f getHoveredControlButtonIconColor() {
    return this.hoveredControlButtonIconColor;
  }

  public TimelineStyle setHoveredControlButtonIconColor(Vector4f hoveredControlButtonIconColor) {
    this.hoveredControlButtonIconColor = hoveredControlButtonIconColor;
    return this;
  }

  public Vector4f getControlButtonBackgroundColor() {
    return this.controlButtonBackgroundColor;
  }

  public TimelineStyle setControlButtonBackgroundColor(Vector4f controlButtonBackgroundColor) {
    this.controlButtonBackgroundColor = controlButtonBackgroundColor;
    return this;
  }

  public Vector4f getHoveredControlButtonBackgroundColor() {
    return this.hoveredControlButtonBackgroundColor;
  }

  public TimelineStyle setHoveredControlButtonBackgroundColor(Vector4f hoveredControlButtonBackgroundColor) {
    this.hoveredControlButtonBackgroundColor = hoveredControlButtonBackgroundColor;
    return this;
  }

  public Vector4f getPressedControlButtonBackgroundColor() {
    return this.pressedControlButtonBackgroundColor;
  }

  private TimelineStyle setPressedControlButtonBackgroundColor(Vector4f pressedControlButtonBackgroundColor) {
    this.pressedControlButtonBackgroundColor = pressedControlButtonBackgroundColor;
    return this;
  }
}
