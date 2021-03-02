package com.raphydaphy.cutsceneapi.cutscene.track;

import com.raphydaphy.cutsceneapi.cutscene.clip.CutsceneClip;

import java.util.List;

public interface CutsceneTrack {
  String getName();
  List<? extends CutsceneClip> getClips();
}
