package com.raphydaphy.cutsceneapi.editor.breakout.properties;

import com.raphydaphy.cutsceneapi.editor.breakout.properties.component.MultiInput;
import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.style.font.FontRegistry;

import static org.liquidengine.legui.style.Style.*;
import static org.liquidengine.legui.style.flex.FlexStyle.*;

public class PropertiesGUI extends Panel {
  public final MultiInput positionInput;
  public final MultiInput rotationInput;

  public PropertiesGUI(int height) {
    super(0, 0, 400, height);

    Label title = new Label("Cutscene Properties", 10, 10, 100, 30);
    title.getStyle().setTextColor(0, 0.5f, 1, 1).setFontSize(30f).setFont(FontRegistry.ROBOTO_BOLD);
    this.add(title);

    Widget cameraProps = new Widget("Camera Properties", 0, 80, 400, 300);
    {
      Component container = cameraProps.getContainer();
      container.getStyle().setDisplay(DisplayType.FLEX).getFlexStyle().setFlexDirection(FlexDirection.COLUMN);

      Label propsTitle = new Label("Camera Properties");
      propsTitle.getStyle().enableFlex(250, 25).setFontSize(25f).setFont(FontRegistry.ROBOTO_BOLD).setMargin(5, 10, 5, 10);
      container.add(propsTitle);

      this.positionInput = new MultiInput("Position", 18,"X", "Y", "Z");
      this.rotationInput = new MultiInput("Rotation", 30,"Pitch", "Yaw");
      container.add(this.positionInput).add(this.rotationInput);

      Button update = new Button("Update");
      update.getStyle().setPosition(PositionType.RELATIVE).setMaxHeight(30).setHeight(30).setMargin(5, 10, 5, 10);
      update.getStyle().setDisplay(DisplayType.FLEX).getFlexStyle().setFlexGrow(1);
      update.getStyle().setHorizontalAlign(HorizontalAlign.CENTER).setVerticalAlign(VerticalAlign.MIDDLE);
      container.add(update);

      this.add(cameraProps);
    }
  }
}
