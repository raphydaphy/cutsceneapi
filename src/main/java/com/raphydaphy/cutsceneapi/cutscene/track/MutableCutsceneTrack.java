package com.raphydaphy.cutsceneapi.cutscene.track;

import com.raphydaphy.cutsceneapi.cutscene.track.keyframe.MutableKeyframe;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MutableCutsceneTrack<T extends MutableKeyframe> implements CutsceneTrack {
  private String name;
  private int length;
  private Map<Integer, T> keyframes = new ConcurrentHashMap<>();

  public MutableCutsceneTrack(String name, int length) {
    this.name = name;
    this.length = length;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setLength(int length) {
    this.length = length;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public int getLength() {
    return this.length;
  }

  @Override
  public Map<Integer, T> getKeyframes() {
    return this.keyframes;
  }

  @Override
  public @Nullable T getKeyframe(int frame) {
    return this.keyframes.get(frame);
  }

  @Override
  public @Nullable T getPrevKeyframe(int frame) {
    for (int f = frame; f >= 0; f--) {
      T keyframe = this.getKeyframe(f);
      if (keyframe != null) return keyframe;
    }
    return null;
  }

  @Override
  public @Nullable T getNextKeyframe(int frame) {
    for (int f = frame + 1; f <= this.length; f++) {
      T keyframe = this.getKeyframe(f);
      if (keyframe != null) return keyframe;
    }
    return null;
  }

  public void setKeyframe(int frame, T keyframe) {
    this.keyframes.put(frame, keyframe);
  }

  public void deleteKeyframe(T keyframe) {
    this.keyframes.remove(keyframe.getFrame());
  }

  public void moveKeyframe(T keyframe, int newFrame) {
    if (newFrame < 0) newFrame = 0;
    else if (newFrame > this.getLength()) newFrame = this.getLength();

    this.deleteKeyframe(keyframe);
    keyframe.setFrame(newFrame);
    this.setKeyframe(newFrame, keyframe);
  }
}
