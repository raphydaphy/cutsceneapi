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

  private float trackHeight = 40;
  private float trackSeparatorSize = 1;
  private Vector4f trackSeparatorColor = new Vector4f(markerColor);

  private Vector2f keyframeSize = new Vector2f(12, 12);
  private Vector4f keyframeColor = ColorUtil.fromInt(127, 194, 126, 1);
  private Vector4f hoveredKeyframeColor = ColorUtil.fromInt(95, 217, 93, 1);

  private Vector4f clipBackgroundColor = ColorUtil.fromInt(209, 125, 201, 1f);
  private Vector4f clipLabelColor = new Vector4f(markerLabelColor);
  private float clipLabelFontSize = 20f;

  private Vector4f clipOutlineColor = new Vector4f(markerLabelColor);
  private Vector4f selectedClipOutlineColor = ColorUtil.fromInt(31, 31, 31, 1f);
  private float clipOutlineWidth = 1f;
  private float selectedClipOutlineWidth = 3f;

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

  public Vector2f getKeyframeSize() {
    return this.keyframeSize;
  }

  public TimelineStyle setKeyframeSize(Vector2f keyframeSize) {
    this.keyframeSize = keyframeSize;
    return this;
  }

  public Vector4f getKeyframeColor() {
    return this.keyframeColor;
  }

  public TimelineStyle setKeyframeColor(Vector4f keyframeColor) {
    this.keyframeColor = keyframeColor;
    return this;
  }

  public Vector4f getHoveredKeyframeColor() {
    return this.hoveredKeyframeColor;
  }

  public TimelineStyle setHoveredKeyframeColor(Vector4f hoveredKeyframeColor) {
    this.hoveredKeyframeColor = hoveredKeyframeColor;
    return this;
  }

  public Vector4f getClipBackgroundColor() {
    return this.clipBackgroundColor;
  }

  public TimelineStyle setClipBackgroundColor(Vector4f clipBackgroundColor) {
    this.clipBackgroundColor = clipBackgroundColor;
    return this;
  }

  public Vector4f getClipLabelColor() {
    return this.clipLabelColor;
  }

  public TimelineStyle setClipLabelColor(Vector4f clipLabelColor) {
    this.clipLabelColor = clipLabelColor;
    return this;
  }

  public float getClipLabelFontSize() {
    return this.clipLabelFontSize;
  }

  public TimelineStyle setClipLabelFontSize(float clipLabelFontSize) {
    this.clipLabelFontSize = clipLabelFontSize;
    return this;
  }

  public Vector4f getClipOutlineColor() {
    return this.clipOutlineColor;
  }

  public TimelineStyle setClipOutlineColor(Vector4f clipOutlineColor) {
    this.clipOutlineColor = clipOutlineColor;
    return this;
  }

  public Vector4f getSelectedClipOutlineColor() {
    return this.selectedClipOutlineColor;
  }

  public TimelineStyle setSelectedClipOutlineColor(Vector4f selectedClipOutlineColor) {
    this.selectedClipOutlineColor = selectedClipOutlineColor;
    return this;
  }


  public float getClipOutlineWidth() {
    return this.clipOutlineWidth;
  }

  public TimelineStyle setClipOutlineWidth(float clipOutlineWidth) {
    this.clipOutlineWidth = clipOutlineWidth;
    return this;
  }

  public float getSelectedClipOutlineWidth() {
    return this.selectedClipOutlineWidth;
  }

  public TimelineStyle setSelectedClipOutlineWidth(float selectedClipOutlineWidth) {
    this.selectedClipOutlineWidth = selectedClipOutlineWidth;
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
