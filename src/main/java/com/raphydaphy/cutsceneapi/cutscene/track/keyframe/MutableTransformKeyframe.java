package com.raphydaphy.cutsceneapi.cutscene.track.keyframe;

import com.raphydaphy.cutsceneapi.cutscene.track.MutableCutsceneTrack;
import com.raphydaphy.cutsceneapi.cutscene.track.property.TransformProperty;

public class MutableTransformKeyframe extends TransformKeyframe implements MutableKeyframe<TransformProperty> {

  public MutableTransformKeyframe(MutableCutsceneTrack track, int frame, TransformProperty prop) {
    super(track, frame, prop);
  }

  @Override
  public void setFrame(int frame) {
    this.frame = frame;
  }

  public MutableCutsceneTrack getTrack() {
    return (MutableCutsceneTrack) this.track;
  }
}
