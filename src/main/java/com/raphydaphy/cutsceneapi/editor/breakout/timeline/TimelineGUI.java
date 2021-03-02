package com.raphydaphy.cutsceneapi.editor.breakout.timeline;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineControls;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineView;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineScrollBar;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.event.TimelineScrollBarMovedEvent;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineViewHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineScrollBarHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineStyle;
import com.raphydaphy.shaded.org.joml.Vector2f;
import com.raphydaphy.shaded.org.joml.Vector2i;
import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.optional.Orientation;
import org.liquidengine.legui.event.Event;
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
      this.onScrollbarMoved(null);

      this.scrollBar.getListenerMap().addListener(TimelineScrollBarMovedEvent.class, this::onScrollbarMoved);
      this.add(this.scrollBar);
    }

    this.addListeners();
  }

  private void addListeners() {
    this.timelineControls.getStepBackButton().addClickListener(this::onStepBackBtn);
    this.timelineControls.getStepForwardButton().addClickListener(this::onStepForwardBtn);
    this.timelineControls.getPlayButton().addClickListener(this::togglePlay);

    this.timelineControls.getGoToStartButton().addClickListener((e) -> {
      this.scrollBar.scrollTo(e.getContext(), 0);
      this.timelineView.snapToFrame(e.getContext(), 0);
    });

    this.timelineControls.getGoToEndButton().addClickListener((e) -> {
      if (this.currentScene == null) return;
      this.scrollBar.scrollTo(e.getContext(), 1);
      this.timelineView.snapToFrame(e.getContext(), this.currentScene.getLength());
    });
  }

  @Override
  public void update(Context context, Frame frame) {
    MutableCutscene cutscene = this.getCurrentScene();
    if (cutscene == null) return;

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
    } else if (cutscene.isPlaying()) {
      if (!this.scrollBar.isDragging()) this.centerCurrentFrameIfHidden(context);
    }
  }

  private void onScrollbarMoved(TimelineScrollBarMovedEvent event) {
    this.timelineView.setScale(this.scrollBar.getScale());
    this.timelineView.setOffset(this.scrollBar.getLeftPercent());
  }

  public void centerCurrentFrameIfHidden(Context context) {
    MutableCutscene cutscene = this.getCurrentScene();
    if (cutscene == null) return;

    Vector2i visibleRange = TimelineViewHelper.getVisibleFrameRange(this.timelineView);

    int visibleLength = visibleRange.y - visibleRange.x;
    int currentFrame = cutscene.getCurrentFrame();

    if (currentFrame < visibleRange.x) {
      int extraFrames = currentFrame - visibleRange.x;
      this.scrollByFrames(extraFrames - visibleLength, context);
    } else if (currentFrame > visibleRange.y) {
      int extraFrames = currentFrame - visibleRange.y;
      this.scrollByFrames(extraFrames + visibleLength, context);
    }
  }

  public void scrollByFrames(int frames, Context context) {
    float absDist = (frames * TimelineViewHelper.getFrameWidth(this.timelineView)) / this.scrollBar.getScale();
    float percent = TimelineScrollBarHelper.distanceToPercent(this.scrollBar, absDist);
    this.scrollBar.scrollBy(context, percent);
  }

  private void onStepBackBtn(MouseClickEvent e) {
    MutableCutscene cutscene = this.getCurrentScene();
    if (cutscene == null) return;

    this.timelineView.snapToFrame(e.getContext(), cutscene.getCurrentFrame() - 1);
    this.centerCurrentFrameIfHidden(e.getContext());
  }

  private void onStepForwardBtn(MouseClickEvent e) {
    MutableCutscene cutscene = this.getCurrentScene();
    if (cutscene == null) return;

    this.timelineView.snapToFrame(e.getContext(), cutscene.getCurrentFrame() + 1);
    this.centerCurrentFrameIfHidden(e.getContext());
  }

  private void togglePlay(Event event) {
    MutableCutscene cutscene = this.getCurrentScene();
    if (cutscene == null) return;

    if (!cutscene.isPlaying() && cutscene.getCurrentFrame() >= cutscene.getLength()) {
      this.timelineView.snapToFrame(event.getContext(), 0);
      this.scrollBar.scrollTo(event.getContext(), 0);
    }

    cutscene.setPlaying(!cutscene.isPlaying());
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
