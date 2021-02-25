package com.raphydaphy.cutsceneapi.editor.breakout.properties.component;

import org.jetbrains.annotations.Nullable;
import org.liquidengine.legui.component.FlexPanel;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.style.flex.FlexStyle;
import org.liquidengine.legui.style.font.FontRegistry;

import java.awt.*;
import java.util.Random;

public class MultiInput extends FlexPanel {
  private String title;
  private String[] fieldNames;
  private float fieldNameWidth;
  private TextInput[] inputFields;

  public MultiInput(String title, int fieldNameWidth, String... fieldNames) {
    super();

    this.title = title;
    this.fieldNames = fieldNames;
    this.fieldNameWidth = fieldNameWidth;
    this.inputFields = new TextInput[fieldNames.length];

    this.initialize();
  }

  private void initialize() {
    this.getStyle().setMaxHeight(40).setMargin(0, 10, 0, 10);
    this.getFlexStyle().setJustifyContent(FlexStyle.JustifyContent.SPACE_BETWEEN).setAlignItems(FlexStyle.AlignItems.CENTER).setFlexGrow(1);

    Label title = new Label(this.title);
    title.getStyle().enableFlex(100, 30).setFont(FontRegistry.ROBOTO_BOLD);
    this.add(title);

    FlexPanel controls = new FlexPanel(150, 30);
    controls.getStyle().setMaximumSize(Float.MAX_VALUE, 30);
    controls.getStyle().getFlexStyle().setFlexGrow(2).setAlignItems(FlexStyle.AlignItems.CENTER).setJustifyContent(FlexStyle.JustifyContent.FLEX_END);

    for (int i = 0; i < this.fieldNames.length; i++) {
      Label fieldLabel = new Label(this.fieldNames[i]);
      fieldLabel.getStyle().enableFlex(this.fieldNameWidth, 20).setHorizontalAlign(HorizontalAlign.RIGHT);

      TextInput input = new TextInput("1");
      input.getStyle().enableFlex(60, 20).setMargin(2.5f);
      controls.add(fieldLabel).add(input);

      this.inputFields[i] = input;
    }

    this.add(controls);
  }

  public TextInput getField(int field) {
    return this.inputFields[field];
  }

  public boolean isFocussed() {
    for (TextInput field : this.inputFields) {
      if (field.isFocused()) return true;
    }
    return false;
  }
}
