package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.cutscene.track.CutsceneTrack;

import java.util.List;

public interface Cutscene {
  List<? extends CutsceneTrack> getTracks();

  void update();
  void setPlaying(boolean playing);

  int getFramerate();
  int getLength();

  int getCurrentFrame();

  boolean isPlaying();

}
