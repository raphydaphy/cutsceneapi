package com.raphydaphy.cutsceneapi.editor.breakout.properties.component.prop;

import org.liquidengine.legui.component.FlexLabel;
import org.liquidengine.legui.component.FlexPanel;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.component.event.label.LabelWidthChangeEvent;
import org.liquidengine.legui.style.flex.FlexStyle;

public class MultiNumericInlineProp extends InlineProp {
  private String[] fieldNames;
  private TextInput[] inputFields;

  public MultiNumericInlineProp(String title, float inputWidth, String... fieldNames) {
    super(title);

    this.fieldNames = fieldNames;
    this.inputFields = new TextInput[fieldNames.length];

    for (int i = 0; i < this.fieldNames.length; i++) {
      FlexPanel control = new FlexPanel(inputWidth, 25);
      control.getFlexStyle().setAlignItems(FlexStyle.AlignItems.CENTER);

      Label fieldLabel = new FlexLabel(this.fieldNames[i]);

      TextInput input = new TextInput("");
      input.getStyle().enableFlex(inputWidth, 20).setMargin(2.5f);

      control.add(fieldLabel).add(input);
      this.getValueContainer().add(control);

      fieldLabel.getListenerMap().addListener(LabelWidthChangeEvent.class, (e) -> {
        control.getStyle().setWidths(e.getWidth() + inputWidth + 5);
      });

      this.inputFields[i] = input;
    }
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
