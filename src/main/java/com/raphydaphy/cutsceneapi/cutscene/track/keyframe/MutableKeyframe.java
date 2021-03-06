package com.raphydaphy.cutsceneapi.cutscene.track.keyframe;

import com.raphydaphy.cutsceneapi.cutscene.track.MutableCutsceneTrack;
import com.raphydaphy.cutsceneapi.cutscene.track.property.Property;

public interface MutableKeyframe<T extends Property> extends Keyframe<T> {
  void setFrame(int frame);

  @Override
  MutableCutsceneTrack getTrack();
}
