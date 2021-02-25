package com.raphydaphy.cutsceneapi.editor.breakout.timeline;

import org.liquidengine.legui.component.*;
import static org.liquidengine.legui.style.Style.*;
import static org.liquidengine.legui.style.flex.FlexStyle.*;

import org.liquidengine.legui.style.font.FontRegistry;

public class TimelineGUI extends Panel {

  public TimelineGUI(int width, int height) {
    super(0, 0, width, height);

    this.getStyle().setDisplay(DisplayType.FLEX).setPosition(PositionType.RELATIVE);
    this.getStyle().getBackground().setColor(1, 0, 0, 1);
    this.getStyle().getFlexStyle().setFlexDirection(FlexDirection.COLUMN).setAlignSelf(AlignSelf.STRETCH);

    Label title = new Label("Cutscene Timeline");
    title.getStyle().setTextColor(0.5f, 1, 0, 1).setFontSize(30f).setFont(FontRegistry.ROBOTO_BOLD);
    title.getStyle().setDisplay(DisplayType.FLEX).setPosition(PositionType.RELATIVE).setWidth(200).setHeight(80);
    this.add(title);

    {
      Label desc = new Label("This is the timeline :)");
      desc.getStyle().setDisplay(DisplayType.FLEX).setPosition(PositionType.RELATIVE).setWidth(200).setHeight(20);
      this.add(desc);
    }

    {
      Label desc = new Label("here is more text");
      desc.getStyle().setDisplay(DisplayType.FLEX).setPosition(PositionType.RELATIVE).setWidth(200).setHeight(20).setTextColor(0, 1, 0, 1);
      this.add(desc);
    }
  }
}
