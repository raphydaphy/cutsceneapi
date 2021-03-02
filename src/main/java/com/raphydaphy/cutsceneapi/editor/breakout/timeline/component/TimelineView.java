package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component;

import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.TimelineGUI;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.event.TimelineHeadMovedEvent;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineViewHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.renderer.TimelineViewRenderer;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineStyle;
import com.raphydaphy.shaded.org.joml.Vector2f;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseDragEvent;
import org.liquidengine.legui.input.Mouse;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.renderer.nvg.NvgRendererProvider;

public class TimelineView extends TimelineComponent {
  private int currentFrame = 0;
  private float scale = 1f;
  private float offset = 0f;

  private boolean draggingHead = false;

  public TimelineView(TimelineGUI timeline) {
    super(timeline);
    this.initialize();
  }

  public TimelineView(TimelineGUI timeline, float x, float y, float width, float height) {
    super(timeline, x, y, width, height);
    this.initialize();
  }

  private void initialize() {
    this.getStyle().setFocusedStrokeColor(null);

    this.getListenerMap().addListener(MouseDragEvent.class, this::handleDrag);
    this.getListenerMap().addListener(MouseClickEvent.class, this::handleClick);
  }

  private void handleDrag(MouseDragEvent event) {
    if (Mouse.MouseButton.MOUSE_BUTTON_LEFT.isPressed() && this.isDraggingHead()) {
      int frame = TimelineViewHelper.getHoveredFrame(this, Mouse.getCursorPosition());
      this.snapToFrame(event.getContext(), frame);
    }
  }

  private void handleClick(MouseClickEvent event) {
    if (event.getAction() == MouseClickEvent.MouseClickAction.RELEASE) {
      this.draggingHead = false;
      return;
    }

    Vector2f cursorPosition = Mouse.getCursorPosition();
    if (!TimelineViewHelper.isMouseOverTop(this, cursorPosition)) return;

    int frame = TimelineViewHelper.getHoveredFrame(this, cursorPosition);
    this.snapToFrame(event.getContext(), frame);

    if (event.getAction() == MouseClickEvent.MouseClickAction.PRESS) {
      this.draggingHead = true;
    }
  }

  public void snapToFrame(Context context, int frame) {
    int oldFrame = this.getCurrentFrame();
    this.setCurrentFrame(frame);

    EventProcessorProvider.getInstance().pushEvent(new TimelineHeadMovedEvent<>(
      this, context, this.getFrame(),
      oldFrame, this.getCurrentFrame()
    ));
  }

  public TimelineView setCurrentFrame(int currentFrame) {
    MutableCutscene cutscene = this.getTimeline().getCurrentScene();
    if (cutscene == null) return this;

    if (currentFrame < 0) currentFrame = 0;
    else if (currentFrame > cutscene.getLength()) currentFrame = cutscene.getLength();

    this.currentFrame = currentFrame;
    return this;
  }

  public TimelineView setScale(float scale) {
    if (scale < 1) scale = 1;

    this.scale = scale;
    return this;
  }

  public TimelineView setOffset(float offset) {
    if (offset < 0) offset = 0;
    else if (offset > 1) offset = 1;

    this.offset = offset;
    return this;
  }

  public int getCurrentFrame() {
    return this.currentFrame;
  }

  public float getScale() {
    return this.scale;
  }

  public float getOffset() {
    return this.offset;
  }

  public Vector2f getOffsetPosition() {
    return new Vector2f(this.getAbsolutePosition()).sub(this.getOffset() * this.getScale() * this.getSize().x, 0);
  }

  public Vector2f getScaledSize() {
    return new Vector2f(this.getSize()).mul(this.getScale(), 1);
  }

  public boolean isDraggingHead() {
    return this.draggingHead;
  }

  static {
    NvgRendererProvider.getInstance().addComponentRenderer(TimelineView.class, new TimelineViewRenderer());
  }
}
