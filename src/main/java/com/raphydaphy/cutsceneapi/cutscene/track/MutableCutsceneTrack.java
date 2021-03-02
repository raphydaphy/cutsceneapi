package com.raphydaphy.cutsceneapi.cutscene.track;

import com.raphydaphy.cutsceneapi.cutscene.clip.CutsceneClip;
import com.raphydaphy.cutsceneapi.cutscene.clip.MutableCutsceneClip;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MutableCutsceneTrack implements CutsceneTrack {
  private String name;
  private List<MutableCutsceneClip> clips = new ArrayList<>();

  public MutableCutsceneTrack(String name) {
    this.name = name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addClip(MutableCutsceneClip clip) {
    this.clips.add(clip);
    this.sortClips();
  }

  public void sortClips() {
    this.clips.sort(Comparator.comparingInt(CutsceneClip::getStartTime));
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public List<MutableCutsceneClip> getClips() {
    return this.clips;
  }
}
