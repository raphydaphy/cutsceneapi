package com.raphydaphy.cutsceneapi.editor.breakout.timeline;

import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelinePanel;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineScrollBar;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.event.TimelineScrollBarMovedEvent;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelinePanelHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineScrollBarHelper;
import com.raphydaphy.shaded.org.joml.Vector2i;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.input.Mouse;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.system.context.Context;

public class TimelineGUI extends Panel {

  private TimelinePanel timelinePanel;
  private TimelineScrollBar scrollBar;

  public TimelineGUI(int width, int height) {
    super(0, 0, width, height);

    this.getStyle().getBackground().setColor(ColorConstants.white());

    this.timelinePanel = new TimelinePanel(10, 10, 800, 200);
    {
      this.add(this.timelinePanel);
    }

    this.scrollBar = new TimelineScrollBar(10, 210, 800, 10);
    {
      this.scrollBar.getListenerMap().addListener(TimelineScrollBarMovedEvent.class, this::onScrollbarMoved);
      this.add(this.scrollBar);
    }
  }

  @Override
  public void update(Context context, Frame frame) {
    if (Mouse.MouseButton.MOUSE_BUTTON_LEFT.isPressed() && this.timelinePanel.isDraggingHead()) {

      int hoveredFrame = TimelinePanelHelper.getHoveredFrame(this.timelinePanel, Mouse.getCursorPosition());
      Vector2i visibleFrames = TimelinePanelHelper.getVisibleFrameRange(this.timelinePanel);

      float scrubSpeed = this.scrollBar.getScrubSpeed() / this.scrollBar.getScale();
      float scrubDistance = TimelineScrollBarHelper.percentToDistance(this.scrollBar, scrubSpeed);
      int scrubbedFrames = Math.round(scrubDistance / TimelinePanelHelper.getFrameWidth(this.timelinePanel));

      if (hoveredFrame <= visibleFrames.x) {
        this.scrollBar.scrollBy(context, -scrubSpeed);
        this.timelinePanel.snapToFrame(context, visibleFrames.x - scrubbedFrames);
      } else if (hoveredFrame >= visibleFrames.y) {
        this.scrollBar.scrollBy(context, scrubSpeed);
        this.timelinePanel.snapToFrame(context, visibleFrames.y + scrubbedFrames);
      }
    }
  }

  private void onScrollbarMoved(TimelineScrollBarMovedEvent event) {
    this.timelinePanel.setScale(this.scrollBar.getScale());
    this.timelinePanel.setOffset(this.scrollBar.getLeftPercent());
  }

  public TimelinePanel getTimelinePanel() {
    return this.timelinePanel;
  }

  public TimelineScrollBar getScrollBar() {
    return this.scrollBar;
  }
}
