package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.cutscene.track.CutsceneTrack;
import com.raphydaphy.cutsceneapi.cutscene.track.keyframe.Keyframe;
import com.raphydaphy.cutsceneapi.cutscene.track.keyframe.TransformKeyframe;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Cutscene {
  List<? extends CutsceneTrack> getTracks();
  CutsceneTrack getCameraTrack();

  void update();
  void updateDelta();

  void setPlaying(boolean playing);

  int getFramerate();
  int getLength();

  int getCurrentFrame();
  int getPreviousFrame();

  boolean isPlaying();

}
