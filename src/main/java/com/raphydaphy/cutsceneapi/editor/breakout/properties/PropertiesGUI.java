package com.raphydaphy.cutsceneapi.editor.breakout.properties;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.style.font.FontRegistry;

public class PropertiesGUI extends Panel {

  public PropertiesGUI(int width, int height) {
    super(0, 0, width, height);

    Label title = new Label("Cutscene Properties", 10, 10, 100, 30);
    title.getStyle().setTextColor(0, 0.5f, 1, 1).setFontSize(30f).setFont(FontRegistry.ROBOTO_BOLD);
    this.add(title);
  }
}
