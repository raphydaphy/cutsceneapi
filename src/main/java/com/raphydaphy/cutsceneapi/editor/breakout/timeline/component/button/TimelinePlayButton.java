package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.button;

import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.TimelineGUI;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineStyle;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.icon.CharIcon;
import org.liquidengine.legui.system.context.Context;

public class TimelinePlayButton extends TimelineButton {
  private static final char PLAY_ICON = 0xf04b;
  private static final char PAUSE_ICON = 0xf04c;

  private CharIcon[] playingIconSet;

  private TimelineGUI timeline;
  private boolean paused = true;

  public TimelinePlayButton(TimelineGUI timeline, TimelineStyle style) {
    super(PLAY_ICON, style);

    this.timeline = timeline;
    this.playingIconSet = this.createIconSet(PAUSE_ICON);
  }

  @Override
  public void update(Context context, Frame frame) {
    MutableCutscene cutscene = this.timeline.getCurrentScene();
    if (cutscene == null) return;

    if (cutscene.isPlaying() == this.isPaused()) {
      this.setPaused(!cutscene.isPlaying());
    }
  }

  public void setPaused(boolean paused) {
    if (paused) this.useIconSet(this.iconSet);
    else this.useIconSet(this.playingIconSet);
    this.paused = paused;
  }

  public boolean isPaused() {
    return this.paused;
  }
}
