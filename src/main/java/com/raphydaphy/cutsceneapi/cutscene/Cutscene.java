package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.cutscene.track.CutsceneTrack;

import java.util.List;

public interface Cutscene {

  int getFramerate();
  int getLength();

  List<CutsceneTrack> getTracks();
}
