package com.raphydaphy.cutsceneapi.editor.breakout.properties.component.prop;

import org.liquidengine.legui.component.TextInput;

public class NumericInlineProp extends InlineProp {
  private final TextInput input;

  public NumericInlineProp(String name, float placeholder) {
    super(name);

    this.input = new TextInput("" + placeholder);
    this.input.getStyle().enableFlex().setHeights(20).setMaxWidth(Float.MAX_VALUE).setMargin(0, 1);
    this.input.getFlexStyle().setFlexGrow(1);

    this.getValueContainer().add(this.input);

  }

  public TextInput getInput() {
    return this.input;
  }
}
