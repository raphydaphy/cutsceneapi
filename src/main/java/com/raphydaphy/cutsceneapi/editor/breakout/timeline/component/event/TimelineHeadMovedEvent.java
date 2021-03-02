package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.event;

import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineView;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.event.Event;
import org.liquidengine.legui.system.context.Context;

public class TimelineHeadMovedEvent<T extends TimelineView> extends Event<T> {

  private final int oldFrame, newFrame;

  public TimelineHeadMovedEvent(T component, Context context, Frame frame, int oldFrame, int newFrame) {
    super(component, context, frame);
    this.oldFrame = oldFrame;
    this.newFrame = newFrame;
  }

  public int getOldFrame() {
    return oldFrame;
  }

  public int getNewFrame() {
    return newFrame;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("oldFrame", this.oldFrame).append("newFrame", this.newFrame).toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TimelineHeadMovedEvent<?> that = (TimelineHeadMovedEvent<?>) o;
    return new EqualsBuilder().appendSuper(super.equals(o)).append(this.oldFrame, that.oldFrame).append(this.newFrame, that.newFrame).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode()).append(this.oldFrame).append(this.newFrame).toHashCode();
  }
}
