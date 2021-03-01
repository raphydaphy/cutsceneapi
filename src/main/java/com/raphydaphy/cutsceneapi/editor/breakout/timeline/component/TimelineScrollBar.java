package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component;

import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineScrollBarHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.renderer.TimelineScrollBarRenderer;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineScrollBarStyle;
import com.raphydaphy.shaded.org.joml.Vector2f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseDragEvent;
import org.liquidengine.legui.input.Mouse;
import org.liquidengine.legui.system.renderer.nvg.NvgRendererProvider;

public class TimelineScrollBar extends Component {
  private float leftPercent = 0f;
  private float rightPercent = 1f;
  private float minSize = 0.05f;

  private DragType currentDrag = null;

  private float leftDragStart, rightDragStart, exactDragStart;

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
  }

  private void handleClick(MouseClickEvent event) {
    Vector2f cursorPosition = Mouse.getCursorPosition();

    if (event.getAction() == MouseClickEvent.MouseClickAction.RELEASE) this.currentDrag = null;
    else if (event.getAction() != MouseClickEvent.MouseClickAction.PRESS) return;

    if (TimelineScrollBarHelper.isMouseOverHandle(this, cursorPosition, this.leftPercent)) {
      this.currentDrag = DragType.LEFT_HANDLE;
    } else if (TimelineScrollBarHelper.isMouseOverHandle(this, cursorPosition, this.rightPercent)) {
      this.currentDrag = DragType.RIGHT_HANDLE;
    } else if (TimelineScrollBarHelper.isMouseOverBar(this, cursorPosition)) {
      this.currentDrag = DragType.BAR;
      this.leftDragStart = this.getLeftPercent();
      this.rightDragStart = this.getRightPercent();
      this.exactDragStart = TimelineScrollBarHelper.getHoveredPercent(this, cursorPosition);
    }
  }

  private void handleDrag(MouseDragEvent event) {
    if (Mouse.MouseButton.MOUSE_BUTTON_LEFT.isPressed() && this.currentDrag != null) {
      float percent = TimelineScrollBarHelper.getHoveredPercent(this, Mouse.getCursorPosition());
      if (this.currentDrag == DragType.LEFT_HANDLE) {
        this.setLeftPercent(percent);
      } else if (this.currentDrag == DragType.RIGHT_HANDLE) {
        this.setRightPercent(percent);
      } else if (currentDrag == DragType.BAR) {
        float delta = percent - this.exactDragStart;
        float newLeft = this.leftDragStart + delta;
        float newRight = this.rightDragStart + delta;

        if (newLeft < 0) {
          this.setLeftPercent(0);
          this.setRightPercent(newRight - newLeft);
        } else if (newRight > 1) {
          this.setLeftPercent(newLeft + 1 - newRight);
          this.setRightPercent(1);
        } else {
          this.setLeftPercent(newLeft);
          this.setRightPercent(newRight);
        }
      }
    }
  }

  // TODO: on change event
  public TimelineScrollBar setLeftPercent(float leftPercent) {
    if (leftPercent < 0) leftPercent = 0;
    else if (leftPercent >= rightPercent - minSize) leftPercent = rightPercent - minSize;

    this.leftPercent = leftPercent;
    return this;
  }

  // TODO: on change event
  public TimelineScrollBar setRightPercent(float rightPercent) {
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

  public float getLeftPercent() {
    return this.leftPercent;
  }

  public float getRightPercent() {
    return this.rightPercent;
  }

  public float getScale() {
    return 1f / (this.rightPercent - this.leftPercent);
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
