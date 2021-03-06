package com.raphydaphy.cutsceneapi.cutscene.track.keyframe;

import com.raphydaphy.cutsceneapi.cutscene.track.CutsceneTrack;
import com.raphydaphy.cutsceneapi.cutscene.track.property.Property;
import org.jetbrains.annotations.Nullable;

public interface Keyframe<T extends Property> {
  CutsceneTrack getTrack();
  int getFrame();

  T getProperty();
  T interpolate(@Nullable Keyframe<T> next, float delta);
}
