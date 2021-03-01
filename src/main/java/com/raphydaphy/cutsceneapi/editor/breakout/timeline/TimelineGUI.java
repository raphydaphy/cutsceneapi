package com.raphydaphy.cutsceneapi.editor.breakout.timeline;

import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelinePanel;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineScrollBar;
import org.liquidengine.legui.component.*;
import org.liquidengine.legui.style.color.ColorUtil;

import static org.liquidengine.legui.style.Style.*;

public class TimelineGUI extends Panel {

  private TimelinePanel timelinePanel;
  private TimelineScrollBar scrollBar;

  public TimelineGUI(int width, int height) {
    super(0, 0, width, height);

    this.getStyle().getBackground().setColor(ColorUtil.fromInt(69, 69, 69, 1));

    this.timelinePanel = new TimelinePanel(10, 10, 800, 200);
    {
      this.add(this.timelinePanel);
    }

    this.scrollBar = new TimelineScrollBar(10, 210, 800, 10);
    {
      this.add(this.scrollBar);
    }
  }

  public TimelinePanel getTimelinePanel() {
    return this.timelinePanel;
  }

  public TimelineScrollBar getScrollBar() {
    return this.scrollBar;
  }
}
