package com.raphydaphy.cutsceneapi.cutscene.track.keyframe;

import com.raphydaphy.cutsceneapi.cutscene.track.property.TransformProperty;
import com.raphydaphy.shaded.org.joml.Vector2f;
import com.raphydaphy.shaded.org.joml.Vector3d;
import org.jetbrains.annotations.Nullable;

public class TransformKeyframe implements Keyframe<TransformProperty> {
  private int frame;
  private TransformProperty prop;

  public TransformKeyframe(int frame, TransformProperty prop) {
    this.frame = frame;
    this.prop = prop;
  }

  @Override
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
    TransformProperty nextProp = next.getProperty();
    return new TransformProperty(
      new Vector3d(this.prop.getPos()).lerp(nextProp.getPos(), delta),
      new Vector2f(this.prop.getRot()).lerp(nextProp.getRot(), delta)
    );
  }
}
