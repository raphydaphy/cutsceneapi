package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component;

import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.TimelineGUI;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.button.TimelineButton;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.button.TimelinePlayButton;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineViewHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineStyle;
import org.liquidengine.legui.component.FlexPanel;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.flex.FlexStyle;
import org.liquidengine.legui.style.font.FontRegistry;
import org.liquidengine.legui.system.context.Context;

public class TimelineControls extends TimelineComponent {
  // https://fontawesome.com/cheatsheet/free/solid
  private static final char STEP_BACK_ICON = 0xf104;
  private static final char STEP_FORWARD_ICON = 0xf105;

  private static final char GO_TO_START_ICON = 0xf049;
  private static final char GO_TO_END_ICON = 0xf050;

  private Label timeLabel;

  private TimelineButton goToStartButton;
  private TimelineButton stepBackButton;
  private TimelineButton playButton;
  private TimelineButton stepForwardButton;
  private TimelineButton goToEndButton;

  public TimelineControls(TimelineGUI timeline) {
    super(timeline);
    this.initialize();
  }

  public TimelineControls(TimelineGUI timeline, float x, float y, float width, float height) {
    super(timeline, x, y, width, height);
    this.initialize();
  }

  private void initialize() {
    TimelineStyle timelineStyle = this.getTimeline().getTimelineStyle();

    this.getStyle().setDisplay(Style.DisplayType.FLEX).enableFlex();
    this.getStyle().setHeights(this.getTimeline().getTimelineStyle().getTopHeight()).setFocusedStrokeColor(null);
    this.getFlexStyle().setFlexDirection(FlexStyle.FlexDirection.COLUMN).setJustifyContent(FlexStyle.JustifyContent.CENTER);

    this.timeLabel = new Label(TimelineViewHelper.formatFrameTime(0, 0));
    {
      this.timeLabel.getStyle().enableFlex(180, 25f).setFontSize(25f).setFont(FontRegistry.ROBOTO_BOLD);
      this.timeLabel.getStyle().setTextColor(this.getTimeline().getTimelineStyle().getHeadColor()).setPadding(5, 5f);

      this.add(timeLabel);
    }

    FlexPanel buttons = new FlexPanel();
    {
      buttons.getStyle().enableFlexGrow(FlexStyle.FlexDirection.ROW).setHeights(timelineStyle.getControlButtonSize().y);
      buttons.getStyle().setMargin(2.5f, 5f);

      this.goToStartButton = new TimelineButton(GO_TO_START_ICON, timelineStyle);
      this.stepBackButton = new TimelineButton(STEP_BACK_ICON, timelineStyle);
      this.playButton = new TimelinePlayButton(this.getTimeline(), timelineStyle);
      this.stepForwardButton = new TimelineButton(STEP_FORWARD_ICON, timelineStyle);
      this.goToEndButton = new TimelineButton(GO_TO_END_ICON, timelineStyle);

      buttons.add(goToStartButton).add(stepBackButton).add(this.playButton).add(stepForwardButton).add(goToEndButton);

      this.add(buttons);
    }
  }

  @Override
  public void update(Context context, Frame frame) {
    MutableCutscene cutscene = this.getTimeline().getCurrentScene();
    if (cutscene == null) return;

    String formattedTime = TimelineViewHelper.formatFrameTime(cutscene.getCurrentFrame(), cutscene.getFramerate());
    this.timeLabel.getTextState().setText(formattedTime);
  }

  public TimelineButton getGoToStartButton() {
    return this.goToStartButton;
  }

  public TimelineButton getStepBackButton() {
    return this.stepBackButton;
  }

  public TimelineButton getPlayButton() {
    return this.playButton;
  }

  public TimelineButton getStepForwardButton() {
    return this.stepForwardButton;
  }

  public TimelineButton getGoToEndButton() {
    return this.goToEndButton;
  }
}
