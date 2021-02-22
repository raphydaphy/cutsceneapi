package com.raphydaphy.cutsceneapi.editor.breakout.timeline;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.style.font.FontRegistry;

public class TimelineGUI extends Panel {

  public TimelineGUI(int width, int height) {
    super(0, 0, width, height);

    Label title = new Label("Cutscene Timeline", 10, 10, 100, 30);
    title.getStyle().setTextColor(0.5f, 1, 0, 1).setFontSize(30f).setFont(FontRegistry.ROBOTO_BOLD);
    this.add(title);
  }
}
