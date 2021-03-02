package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component;

import com.raphydaphy.cutsceneapi.editor.breakout.timeline.TimelineGUI;
import org.liquidengine.legui.component.Component;

public class TimelineComponent extends Component {
  private TimelineGUI timeline;

  public TimelineComponent(TimelineGUI timeline) {
    super();
    this.timeline = timeline;
  }

  public TimelineComponent(TimelineGUI timeline, float x, float y, float width, float height) {
    super(x, y, width, height);
    this.timeline = timeline;
  }

  public TimelineGUI getTimeline() {
    return this.timeline;
  }
}
