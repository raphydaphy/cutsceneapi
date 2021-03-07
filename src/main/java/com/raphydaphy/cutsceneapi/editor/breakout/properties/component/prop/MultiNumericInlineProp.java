package com.raphydaphy.cutsceneapi.editor.breakout.properties.component.prop;

import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.event.label.LabelWidthChangeEvent;
import org.liquidengine.legui.component.event.textinput.NumericInputContentChangeEvent;
import org.liquidengine.legui.listener.EventListener;
import org.liquidengine.legui.style.flex.FlexStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MultiNumericInlineProp<T extends Number> extends InlineProp {
  private final String[] fieldNames;
  private final List<NumericInput<T>> inputFields;

  public MultiNumericInlineProp(String title, float inputWidth, T placeholder, String... fieldNames) {
    super(title);

    this.fieldNames = fieldNames;
    this.inputFields = new ArrayList<>();

    for (String fieldName : this.fieldNames) {
      FlexPanel control = new FlexPanel(inputWidth, 25);
      control.getFlexStyle().setAlignItems(FlexStyle.AlignItems.CENTER);

      Label fieldLabel = new FlexLabel(fieldName);

      NumericInput<T> input = new NumericInput<>(placeholder);
      input.getStyle().enableFlex(inputWidth, 20).setMargin(2.5f);

      control.add(fieldLabel).add(input);
      this.getValueContainer().add(control);

      fieldLabel.getListenerMap().addListener(LabelWidthChangeEvent.class, (e) -> {
        control.getStyle().setWidths(e.getWidth() + inputWidth + 5);
      });

      this.inputFields.add(input);
    }
  }

  public NumericInput<T> getField(int field) {
    return this.inputFields.get(field);
  }

  public boolean isFocussed() {
    for (TextInput field : this.inputFields) {
      if (field.isFocused()) return true;
    }
    return false;
  }

  public void addChangeListener(int field, EventListener<NumericInputContentChangeEvent<T, NumericInput<T>>> eventListener) {
    this.getField(field).addValueChangeListener(eventListener);
  }
}
