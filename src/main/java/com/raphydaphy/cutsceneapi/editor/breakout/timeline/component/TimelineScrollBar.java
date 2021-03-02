package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component;

import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.event.TimelineScrollBarMovedEvent;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineScrollBarHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.renderer.TimelineScrollBarRenderer;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineScrollBarStyle;
import com.raphydaphy.shaded.org.joml.Vector2f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.cursor.StandardCursor;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseDragEvent;
import org.liquidengine.legui.input.Mouse;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.renderer.nvg.NvgRendererProvider;

public class TimelineScrollBar extends Component {
  private float leftPercent = 0f;
  private float rightPercent = 1f;
  private float minSize = 0.05f;
  private float scrubSpeed = 0.01f;

  private DragType currentDrag = null;

  private float leftDragStart, exactDragStart;

  private TimelineScrollBarStyle scrollBarStyle = new TimelineScrollBarStyle();

  public TimelineScrollBar() {
    this.initialize();
  }

  public TimelineScrollBar(float x, float y, float width, float height) {
    this.initialize();
    this.setPosition(x, y).setSize(width, height);
  }

  private void initialize() {
    this.getStyle().setFocusedStrokeColor(null);

    this.getListenerMap().addListener(MouseClickEvent.class, this::handleClick);
    this.getListenerMap().addListener(MouseDragEvent.class, this::handleDrag);
    this.getListenerMap().addListener(CursorEnterEvent.class, this::onCursorEnter);
  }

  private void handleClick(MouseClickEvent event) {
    Vector2f cursorPosition = Mouse.getCursorPosition();

    if (event.getAction() == MouseClickEvent.MouseClickAction.RELEASE) {
      this.setCursor(event.getContext(), null);
      this.currentDrag = null;
      return;
    } else if (event.getAction() != MouseClickEvent.MouseClickAction.PRESS) return;

    if (TimelineScrollBarHelper.isMouseOverHandle(this, cursorPosition, this.leftPercent)) {
      this.startDragging(event.getContext(), DragType.LEFT_HANDLE);
      return;
    } else if (TimelineScrollBarHelper.isMouseOverHandle(this, cursorPosition, this.rightPercent)) {
      this.startDragging(event.getContext(), DragType.RIGHT_HANDLE);
      return;
    } else if (TimelineScrollBarHelper.isMouseOverBar(this, cursorPosition)) {
      this.startDragging(event.getContext(), DragType.BAR);
      this.leftDragStart = this.getLeftPercent();
      this.exactDragStart = TimelineScrollBarHelper.getHoveredPercent(this, cursorPosition);
      return;
    }

    float leftPos = TimelineScrollBarHelper.percentToAbsolutePos(this, this.getLeftPercent());
    float rightPos = TimelineScrollBarHelper.percentToAbsolutePos(this, this.getRightPercent());

    if (cursorPosition.x < leftPos) {
      this.scrollBy(event.getContext(), -this.scrubSpeed);
    } else if (cursorPosition.x > rightPos) {
      this.scrollBy(event.getContext(), this.scrubSpeed);
    }
  }

  private void startDragging(Context context, DragType type) {
    this.currentDrag = type;
    this.setCursor(context, type == DragType.BAR ? StandardCursor.HAND : StandardCursor.H_RESIZE);
  }

  private void handleDrag(MouseDragEvent event) {
    if (Mouse.MouseButton.MOUSE_BUTTON_LEFT.isPressed() && this.currentDrag != null) {
      float percent = TimelineScrollBarHelper.getHoveredPercent(this, Mouse.getCursorPosition());

      if (this.currentDrag == DragType.BAR) {
        float delta = percent - this.exactDragStart;
        float newLeft = this.leftDragStart + delta;
        this.scrollTo(event.getContext(), newLeft);
      } else {
        float oldLeftPercent = this.getLeftPercent();
        float oldRightPercent = this.getRightPercent();

        if (this.currentDrag == DragType.LEFT_HANDLE) {
          this.setLeftPercent(percent);
        } else if (this.currentDrag == DragType.RIGHT_HANDLE) {
          this.setRightPercent(percent);
        }
        this.updatePos(event.getContext(), oldLeftPercent, oldRightPercent);
      }
    }
  }



  @Override
  public void update(Context context, Frame frame) {
    if (this.isDragging() || !this.isHovered()) return;
    Vector2f mousePosition = Mouse.getCursorPosition();

    if (TimelineScrollBarHelper.isMouseOverEitherHandle(this, mousePosition)) {
      this.setCursor(context, StandardCursor.H_RESIZE);
    } else if (TimelineScrollBarHelper.isMouseOverBar(this, mousePosition)) {
      this.setCursor(context, StandardCursor.HAND);
    } else this.setCursor(context, null);
  }

  private void onCursorEnter(CursorEnterEvent event) {
    if (!event.isEntered() && !this.isDragging()) {
      this.setCursor(event.getContext(), null);
    }
  }

  private boolean isDragging() {
    return this.currentDrag != null;
  }

  public void scrollTo(Context context, float newLeftPercent) {
    float oldLeftPercent = this.getLeftPercent();
    float oldRightPercent = this.getRightPercent();

    float width = oldRightPercent - oldLeftPercent;
    float newRightPercent = newLeftPercent + width;

    if (newLeftPercent < 0) {
      this.setLeftPercent(0);
      this.setRightPercent(newRightPercent - newLeftPercent);
    } else if (newRightPercent > 1) {
      this.setLeftPercent(newLeftPercent + 1 - newRightPercent);
      this.setRightPercent(1);
    } else {
      this.setLeftPercent(newLeftPercent);
      this.setRightPercent(newRightPercent);
    }

    this.updatePos(context, oldLeftPercent, oldRightPercent);
  }

  public void scrollBy(Context context, float percent) {
    this.scrollTo(context, this.getLeftPercent() + percent);
  }

  private void updatePos(Context context, float oldLeftPercent, float oldRightPercent) {
    float newLeftPercent = this.getLeftPercent();
    float newRightPercent = this.getRightPercent();

    if (newLeftPercent != oldLeftPercent || newRightPercent != oldRightPercent) {
      EventProcessorProvider.getInstance().pushEvent(new TimelineScrollBarMovedEvent<>(
        this, context, this.getFrame(),
        oldLeftPercent, oldRightPercent, newLeftPercent, newRightPercent
      ));
    }
  }

  private TimelineScrollBar setLeftPercent(float leftPercent) {
    if (leftPercent < 0) leftPercent = 0;
    else if (leftPercent >= rightPercent - minSize) leftPercent = rightPercent - minSize;

    this.leftPercent = leftPercent;
    return this;
  }

  private TimelineScrollBar setRightPercent(float rightPercent) {
    if (rightPercent > 1) rightPercent = 1;
    else if (rightPercent <= leftPercent + minSize) rightPercent = leftPercent + minSize;

    this.rightPercent = rightPercent;

    return this;
  }

  public TimelineScrollBar setMinSize(float minSize) {
    if (minSize < 0.0001f) minSize = 0.0001f;
    else if (minSize > 1) minSize = 1;

    this.minSize = minSize;
    return this;
  }

  public TimelineScrollBar setScrubSpeed(float scrubSpeed) {
    this.scrubSpeed = scrubSpeed;
    return this;
  }

  public float getLeftPercent() {
    return this.leftPercent;
  }

  public float getRightPercent() {
    return this.rightPercent;
  }

  public float getScale() {
    return 1f / (this.rightPercent - this.leftPercent);
  }

  public float getScrubSpeed() {
    return this.scrubSpeed;
  }

  public TimelineScrollBarStyle getScrollBarStyle() {
    return this.scrollBarStyle;
  }

  static {
    NvgRendererProvider.getInstance().addComponentRenderer(TimelineScrollBar.class, new TimelineScrollBarRenderer());
  }

  private enum DragType {
    LEFT_HANDLE, BAR, RIGHT_HANDLE
  }

  ;
}
