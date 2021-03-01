package com.raphydaphy.cutsceneapi.editor.breakout.timeline;

import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelinePanel;
import org.liquidengine.legui.component.*;

import static org.liquidengine.legui.style.Style.*;

public class TimelineGUI extends Panel {

  private TimelinePanel timelinePanel;

  public TimelineGUI(int width, int height) {
    super(0, 0, width, height);

    this.getStyle().setDisplay(DisplayType.FLEX).enableFlex();
    this.getStyle().setMaxWidth(Float.MAX_VALUE).setHeights(height);
    this.getStyle().getBackground().setColor(0, 1, 0, 0.2f);

    FlexPanel left = new FlexPanel();
    {
      left.getStyle().setWidths(150).setHeights(300);
      left.getStyle().getBackground().setColor(1, 0, 0, 0.2f);

      this.add(left);
    }

    FlexPanel right = new FlexPanel();
    {
      right.getStyle().setDisplay(DisplayType.MANUAL);
      right.getStyle().getBackground().setColor(0, 0, 1, 0.2f);

      right.setSize(500, 300);
      right.getStyle().setWidths(500).setHeights(300);

      this.timelinePanel = new TimelinePanel();
      this.timelinePanel.setPosition(0, 0);
      this.timelinePanel.setSize(400, 250);

      right.add(this.timelinePanel);
      this.add(right);
    }

  }

  public TimelinePanel getTimelinePanel() {
    return this.timelinePanel;
  }
}
