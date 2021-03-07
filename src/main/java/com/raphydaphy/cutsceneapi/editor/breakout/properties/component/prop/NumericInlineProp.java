package com.raphydaphy.cutsceneapi.editor.breakout.properties.component.prop;

import org.liquidengine.legui.component.NumericInput;

public class NumericInlineProp<T extends Number> extends InlineProp {
  private final NumericInput<T> input;

  public NumericInlineProp(String name, T placeholder) {
    super(name);

    this.input = new NumericInput<>(placeholder);
    this.input.getStyle().enableFlex().setHeights(20).setMaxWidth(Float.MAX_VALUE).setMargin(0, 1);
    this.input.getFlexStyle().setFlexGrow(1);

    this.getValueContainer().add(this.input);

  }

  public NumericInput<T> getInput() {
    return this.input;
  }
}
