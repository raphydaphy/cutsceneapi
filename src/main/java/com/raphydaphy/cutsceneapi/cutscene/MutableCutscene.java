package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.track.CutsceneTrack;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public class MutableCutscene implements Cutscene {
  private List<CutsceneTrack> tracks;
  private int framerate, length, currentFrame;
  private boolean playing;

  public MutableCutscene() {
    this(30, 30 * 10);
  }

  public MutableCutscene(int framerate, int length) {
    this.setFramerate(framerate).setLength(length);

    this.tracks = new ArrayList<>();
    this.addTrack(new CutsceneTrack("Camera"));
  }

  @Override
  public void update() {
    if (this.isPlaying()) {
      if (this.currentFrame < this.length) {
        this.setCurrentFrame(this.getCurrentFrame() + 1);
      } else {
        this.setPlaying(false);
      }
    }
  }

  public void addTrack(CutsceneTrack track) {
    this.tracks.add(track);
  }

  public void addTrack(CutsceneTrack track, int index) {
    List<CutsceneTrack> newTracks = new ArrayList<>();
    int length = this.tracks.size();

    if (index < 0) index = 0;
    else if (index > length) index = length;

    for (int i = 0; i < length; i++) {
      if (i == index) newTracks.add(track);
      newTracks.add(this.tracks.get(i));
    }

    this.tracks = newTracks;
  }

  public void removeTrack(CutsceneTrack track) {
    this.tracks.remove(track);
  }

  public MutableCutscene setFramerate(int framerate) {
    CutsceneAPI.LOGGER.info("Set cutscene framerate to " + framerate + "fps");
    MinecraftClient.getInstance().getWindow().setFramerateLimit(framerate);
    this.framerate = framerate;
    return this;
  }

  public MutableCutscene setLength(int length) {
    CutsceneAPI.LOGGER.info("Set cutscene length to " + length + " frames");
    this.length = length;
    return this;
  }

  public void setCurrentFrame(int currentFrame) {
    if (currentFrame < 0) currentFrame = 0;
    else if (currentFrame > this.length) currentFrame = this.length;
    this.currentFrame = currentFrame;
  }

  @Override
  public void setPlaying(boolean playing) {
    this.playing = playing;
  }

  @Override
  public List<CutsceneTrack> getTracks() {
    return this.tracks;
  }

  @Override
  public int getFramerate() {
    return this.framerate;
  }

  @Override
  public int getLength() {
    return this.length;
  }

  @Override
  public int getCurrentFrame() {
    return this.currentFrame;
  }

  @Override
  public boolean isPlaying() {
    return this.playing;
  }
}
