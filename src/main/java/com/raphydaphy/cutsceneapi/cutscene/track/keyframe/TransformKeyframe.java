package com.raphydaphy.cutsceneapi.cutscene.track.keyframe;

import com.raphydaphy.cutsceneapi.cutscene.track.CutsceneTrack;
import com.raphydaphy.cutsceneapi.cutscene.track.property.TransformProperty;
import org.jetbrains.annotations.Nullable;

public class TransformKeyframe implements Keyframe<TransformProperty> {
  protected CutsceneTrack track;
  protected int frame;
  protected TransformProperty prop;

  public TransformKeyframe(CutsceneTrack track, int frame, TransformProperty prop) {
    this.track = track;
    this.frame = frame;
    this.prop = prop;
  }

  public CutsceneTrack getTrack() {
    return this.track;
  }

  public int getFrame() {
    return this.frame;
  }

  @Override
  public TransformProperty getProperty() {
    return this.prop;
  }

  @Override
  public TransformProperty interpolate(@Nullable Keyframe<TransformProperty> next, float delta) {
    if (next == null) return this.prop;
    return this.getProperty().interpolate(next.getProperty(), delta);
  }
}
