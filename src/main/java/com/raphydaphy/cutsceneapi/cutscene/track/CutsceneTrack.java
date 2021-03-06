package com.raphydaphy.cutsceneapi.cutscene.track;

import com.raphydaphy.cutsceneapi.cutscene.track.keyframe.Keyframe;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface CutsceneTrack<T extends Keyframe> {
  String getName();
  int getLength();

  Map<Integer, T> getKeyframes();
  @Nullable T getKeyframe(int frame);

  @Nullable T getPrevKeyframe(int frame);
  @Nullable T getNextKeyframe(int frame);
}
