package com.raphydaphy.cutsceneapi.editor.breakout.timeline;

import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineControls;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineView;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineScrollBar;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.event.TimelineScrollBarMovedEvent;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineViewHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineScrollBarHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineStyle;
import com.raphydaphy.shaded.org.joml.Vector2i;
import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.optional.Orientation;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.WindowSizeEvent;
import org.liquidengine.legui.input.Mouse;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.system.context.Context;

import static org.liquidengine.legui.style.Style.*;
import static org.liquidengine.legui.style.flex.FlexStyle.*;

public class TimelineGUI extends Panel {
  private MutableCutscene currentScene = null;
  private TimelineStyle timelineStyle = new TimelineStyle();

  private SplitPanel splitPanel;

  private TimelineControls timelineControls;
  private TimelineView timelineView;

  private TimelineScrollBar scrollBar;

  public TimelineGUI(int width, int height) {
    super(0, 0, width, height);

    this.getStyle().getBackground().setColor(ColorConstants.white());
    this.getStyle().setDisplay(DisplayType.FLEX).setMaxWidth(Float.MAX_VALUE);
    this.getFlexStyle().setFlexDirection(FlexDirection.COLUMN).setFlexGrow(1);

    float initialScrollBarHeight = 10f;
    float initialSplitPanelHeight = height - initialScrollBarHeight;

    this.splitPanel = new SplitPanel(Orientation.HORIZONTAL);
    {
      this.splitPanel.setRatio(25f);
      this.splitPanel.getStyle().setHeights(initialSplitPanelHeight).enableFlexGrow(FlexDirection.ROW);

      this.splitPanel.getTopLeft().getStyle().setDisplay(DisplayType.FLEX).getFlexStyle().setFlexDirection(FlexDirection.COLUMN);
      this.splitPanel.getBottomRight().getStyle().setDisplay(DisplayType.FLEX);

      this.timelineControls = new TimelineControls(this);
      {
        this.timelineControls.getStyle().enableFlexGrow(FlexDirection.ROW);
        this.splitPanel.getTopLeft().add(this.timelineControls);

        FlexPanel divider = new FlexPanel();
        divider.getStyle().enableFlexGrow(FlexDirection.ROW).setHeights(timelineStyle.getBaselineSize());
        divider.getStyle().getBackground().setColor(timelineStyle.getBaselineColor());
        this.splitPanel.getTopLeft().add(divider);
      }

      this.timelineView = new TimelineView(this);
      {
        this.timelineView.getStyle().setHeights(initialSplitPanelHeight).enableFlexGrow(FlexDirection.ROW);
        splitPanel.getBottomRight().add(this.timelineView);
      }

      this.splitPanel.getListenerMap().addListener(WindowSizeEvent.class, (e) -> {
        float newHeight = e.getHeight() - this.scrollBar.getSize().y;

        this.splitPanel.getStyle().setHeights(newHeight);

        this.timelineView.getStyle().setHeights(newHeight);
      });

      this.add(splitPanel);
    }

    this.scrollBar = new TimelineScrollBar(this);
    {
      this.scrollBar.getStyle().setHeights(initialScrollBarHeight).enableFlexGrow(FlexDirection.ROW);
      this.scrollBar.setRightPercentForcefully(0.5f);

      this.scrollBar.getListenerMap().addListener(TimelineScrollBarMovedEvent.class, this::onScrollbarMoved);
      this.add(this.scrollBar);
    }

    this.timelineControls.getGoToStartButton().addClickListener((e) -> {
      this.scrollBar.scrollTo(e.getContext(), 0);
      this.timelineView.snapToFrame(e.getContext(), 0);
    });

    this.timelineControls.getStepBackButton().addClickListener(this::stepBack);
    this.timelineControls.getStepForwardButton().addClickListener(this::stepForward);

    this.timelineControls.getGoToEndButton().addClickListener((e) -> {
      if (this.currentScene == null) return;
      this.scrollBar.scrollTo(e.getContext(), 1);
      this.timelineView.snapToFrame(e.getContext(), this.currentScene.getLength());
    });
  }

  @Override
  public void update(Context context, Frame frame) {
    if (Mouse.MouseButton.MOUSE_BUTTON_LEFT.isPressed() && this.timelineView.isDraggingHead()) {

      int hoveredFrame = TimelineViewHelper.getHoveredFrame(this.timelineView, Mouse.getCursorPosition());
      Vector2i visibleFrames = TimelineViewHelper.getVisibleFrameRange(this.timelineView);

      float scrubSpeed = this.scrollBar.getScrubSpeed() / this.scrollBar.getScale();
      float scrubDistance = TimelineScrollBarHelper.percentToDistance(this.scrollBar, scrubSpeed);
      int scrubbedFrames = Math.round(scrubDistance / TimelineViewHelper.getFrameWidth(this.timelineView));

      if (hoveredFrame <= visibleFrames.x) {
        this.scrollBar.scrollBy(context, -scrubSpeed);
        this.timelineView.snapToFrame(context, visibleFrames.x - scrubbedFrames);
      } else if (hoveredFrame >= visibleFrames.y) {
        this.scrollBar.scrollBy(context, scrubSpeed);
        this.timelineView.snapToFrame(context, visibleFrames.y + scrubbedFrames);
      }
    }
  }

  private void onScrollbarMoved(TimelineScrollBarMovedEvent event) {
    this.timelineView.setScale(this.scrollBar.getScale());
    this.timelineView.setOffset(this.scrollBar.getLeftPercent());
  }

  private void stepBack(MouseClickEvent e) {
    this.timelineView.snapToFrame(e.getContext(), this.timelineView.getCurrentFrame() - 1);
    int frameDist = this.timelineView.getCurrentFrame() - TimelineViewHelper.getVisibleFrameRange(this.timelineView).x;
    if (frameDist < 0) {
      float absDist = frameDist * TimelineViewHelper.getFrameWidth(this.timelineView);
      float percent = TimelineScrollBarHelper.distanceToPercent(this.scrollBar, absDist);
      this.scrollBar.scrollBy(e.getContext(), percent);
    }
  }

  private void stepForward(MouseClickEvent e) {
    this.timelineView.snapToFrame(e.getContext(), this.timelineView.getCurrentFrame() + 1);
    int frameDist = TimelineViewHelper.getVisibleFrameRange(this.timelineView).y - this.timelineView.getCurrentFrame();
    if (frameDist < 0) {
      float absDist = frameDist * TimelineViewHelper.getFrameWidth(this.timelineView);
      float percent = TimelineScrollBarHelper.distanceToPercent(this.scrollBar, absDist);
      this.scrollBar.scrollBy(e.getContext(), -percent);
    }
  }

  public TimelineGUI setCurrentScene(MutableCutscene cutscene) {
    this.currentScene = cutscene;
    return this;
  }

  public MutableCutscene getCurrentScene() {
    return this.currentScene;
  }

  public TimelineControls getTimelineControls() {
    return this.timelineControls;
  }

  public TimelineView getTimelineView() {
    return this.timelineView;
  }

  public TimelineScrollBar getScrollBar() {
    return this.scrollBar;
  }

  public TimelineStyle getTimelineStyle() {
    return this.timelineStyle;
  }
}
