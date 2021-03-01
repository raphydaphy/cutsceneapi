package com.raphydaphy.cutsceneapi.editor.breakout.properties.component.prop;

import org.liquidengine.legui.component.FlexPanel;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.flex.FlexStyle;
import org.liquidengine.legui.style.font.FontRegistry;

public class InlineProp extends FlexPanel {
  public static final float WIDTH = 250;

  private String title;
  private FlexPanel valueContainer;

  public InlineProp(String title) {
    super();

    this.title = title;

    this.initialize();
  }

  private void initialize() {
    this.getStyle().setMaxHeight(40).setMargin(0, 5, 0, 5);
    this.getFlexStyle().setJustifyContent(FlexStyle.JustifyContent.SPACE_BETWEEN).setAlignItems(FlexStyle.AlignItems.CENTER).setFlexGrow(1);

    Label title = new Label(this.title);
    title.getStyle().setPosition(Style.PositionType.RELATIVE).setMaxHeight(30f).setMinWidth(125).setFont(FontRegistry.ROBOTO_BOLD);
    title.getFlexStyle().setFlexGrow(1);
    this.add(title);

    this.valueContainer = new FlexPanel(WIDTH, 25);
    valueContainer.getStyle().getFlexStyle().setAlignItems(FlexStyle.AlignItems.CENTER).setJustifyContent(FlexStyle.JustifyContent.SPACE_BETWEEN);

    this.add(valueContainer);
  }

  public FlexPanel getValueContainer() {
    return this.valueContainer;
  }
}

