package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.event;

import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineScrollBar;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.event.Event;
import org.liquidengine.legui.system.context.Context;

public class TimelineScrollBarMovedEvent<T extends TimelineScrollBar> extends Event<T> {
  private final float oldLeftPercent, oldRightPercent;
  private final float newLeftPercent, newRightPercent;

  public TimelineScrollBarMovedEvent(T component, Context context, Frame frame, float oldLeftPercent, float oldRightPercent, float newLeftPercent, float newRightPercent) {
    super(component, context, frame);

    this.oldLeftPercent = oldLeftPercent;
    this.oldRightPercent = oldRightPercent;

    this.newLeftPercent = newLeftPercent;
    this.newRightPercent = newRightPercent;
  }

  public float getOldLeftPercent() {
    return this.oldLeftPercent;
  }

  public float getOldRightPercent() {
    return this.oldRightPercent;
  }

  public float getNewLeftPercent() {
    return this.newLeftPercent;
  }

  public float getNewRightPercent() {
    return this.newRightPercent;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
      .append("oldLeftPercent", this.oldLeftPercent).append("oldRightPercent", this.oldRightPercent)
      .append("newLeftPercent", this.newLeftPercent).append("newRightPercent", this.newRightPercent)
      .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TimelineScrollBarMovedEvent<?> that = (TimelineScrollBarMovedEvent<?>) o;
    return new EqualsBuilder().appendSuper(super.equals(o))
      .append(this.oldLeftPercent, that.oldLeftPercent).append(this.oldRightPercent, that.oldRightPercent)
      .append(this.newLeftPercent, that.newLeftPercent).append(this.newRightPercent, that.newRightPercent).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).appendSuper(super.hashCode())
      .append(this.oldLeftPercent).append(this.oldRightPercent)
      .append(this.newLeftPercent).append(this.newRightPercent).toHashCode();
  }
}
