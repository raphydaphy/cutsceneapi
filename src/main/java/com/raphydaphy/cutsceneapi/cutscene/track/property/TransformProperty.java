package com.raphydaphy.cutsceneapi.cutscene.track.property;

import com.raphydaphy.shaded.org.joml.Vector2f;
import com.raphydaphy.shaded.org.joml.Vector3d;

public class TransformProperty implements Property {
  private Vector3d pos;
  private Vector2f rot;

  public TransformProperty(Vector3d pos, float pitch, float yaw) {
    this(pos, new Vector2f(pitch, yaw));
  }

  public TransformProperty(Vector3d pos, Vector2f rot) {
    this.pos = pos;
    this.rot = rot;
  }

  public Vector3d getPos() {
    return this.pos;
  }

  public TransformProperty setPos(Vector3d pos) {
    this.pos = pos;
    return this;
  }

  public float getPitch() {
    return this.rot.x;
  }

  public float getYaw() {
    return this.rot.y;
  }

  public TransformProperty setPitch(float pitch) {
    this.rot.x = pitch;
    return this;
  }

  public TransformProperty setYaw(float yaw) {
    this.rot.y = yaw;
    return this;
  }

  public Vector2f getRot() {
    return this.rot;
  }

  public TransformProperty setRot(Vector2f rot) {
    this.rot = rot;
    return this;
  }
}
