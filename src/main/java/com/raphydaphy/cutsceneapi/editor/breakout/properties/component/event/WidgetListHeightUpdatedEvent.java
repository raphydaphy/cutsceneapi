package com.raphydaphy.cutsceneapi.editor.breakout.properties.component.event;

import com.raphydaphy.cutsceneapi.editor.breakout.properties.component.FixedWidgetList;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.event.Event;
import org.liquidengine.legui.system.context.Context;

public class WidgetListHeightUpdatedEvent<T extends FixedWidgetList> extends Event<T> {
  private float oldHeight, newHeight;

  public WidgetListHeightUpdatedEvent(T component, Context context, Frame frame, float oldHeight, float newHeight) {
    super(component, context, frame);
    this.oldHeight = oldHeight;
    this.newHeight = newHeight;
  }

  public float getOldHeight() {
    return this.oldHeight;
  }

  public float getNewHeight() {
    return this.newHeight;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("oldHeight", this.oldHeight).append("newHeight", this.newHeight).toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WidgetListHeightUpdatedEvent<?> that = (WidgetListHeightUpdatedEvent<?>) o;
    return new EqualsBuilder().appendSuper(super.equals(o)).append(this.oldHeight, that.oldHeight).append(this.newHeight, that.newHeight).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(this.oldHeight).append(this.newHeight).toHashCode();
  }
}
