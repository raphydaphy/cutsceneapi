package com.raphydaphy.cutsceneapi.cutscene.clip;

public class MutableCutsceneClip implements CutsceneClip {
  private String name;
  private int startTime, length;

  private boolean selected;

  public MutableCutsceneClip(String name, int startTime, int length) {
    this.name = name;
    this.startTime = startTime;
    this.length = length;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setStartTime(int startTime) {
    this.startTime = startTime;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getName() {
    return this.name;
  }

  public int getStartTime() {
    return this.startTime;
  }

  public int getLength() {
    return this.length;
  }

  public boolean isSelected() {
    return this.selected;
  }
}
