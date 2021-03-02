package com.raphydaphy.cutsceneapi.cutscene.track;

public class CutsceneTrack {
  private String name;

  public CutsceneTrack(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public CutsceneTrack setName(String name) {
    this.name = name;
    return this;
  }
}
