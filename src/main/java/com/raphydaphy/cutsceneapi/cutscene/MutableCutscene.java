package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneAPI;

public class MutableCutscene implements Cutscene {
  private int framerate, length;

  public MutableCutscene() {
    this(30, 30 * 10);
  }

  public MutableCutscene(int framerate, int length) {
    this.framerate = framerate;
    this.length = length;
  }

  public MutableCutscene setFramerate(int framerate) {
    CutsceneAPI.LOGGER.info("Set cutscene framerate to " + framerate + "fps");
    this.framerate = framerate;
    return this;
  }

  public MutableCutscene setLength(int length) {
    CutsceneAPI.LOGGER.info("Set cutscene length to " + length + " frames");
    this.length = length;
    return this;
  }

  @Override
  public int getFramerate() {
    return this.framerate;
  }

  @Override
  public int getLength() {
    return this.length;
  }
}
