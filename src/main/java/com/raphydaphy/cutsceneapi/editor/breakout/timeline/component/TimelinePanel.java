package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component;

import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.event.TimelineHeadMovedEvent;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelinePanelHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.renderer.TimelinePanelRenderer;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineStyle;
import com.raphydaphy.shaded.org.joml.Vector2f;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.event.Event;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseDragEvent;
import org.liquidengine.legui.input.Mouse;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.system.renderer.nvg.NvgRendererProvider;

public class TimelinePanel extends Panel {
  private MutableCutscene currentScene;
  private int currentFrame = 0;
  private float scale = 1f;
  private float offset = 0f;

  private TimelineStyle timelineStyle = new TimelineStyle();

  public TimelinePanel() {
    this(null);
  }

  public TimelinePanel(MutableCutscene currentScene) {
    this.currentScene = currentScene;

    this.getListenerMap().addListener(MouseClickEvent.class, this::snapHeadToCursor);
    this.getListenerMap().addListener(MouseDragEvent.class, (e) -> {
      if (Mouse.MouseButton.MOUSE_BUTTON_LEFT.isPressed()) this.snapHeadToCursor(e);
    });
  }

  private void snapHeadToCursor(Event event) {
    Vector2f cursorPosition = Mouse.getCursorPosition();
    if (!TimelinePanelHelper.isMouseOverTop(this, cursorPosition)) return;

    int frame = TimelinePanelHelper.getHoveredFrame(this, cursorPosition);
    int oldFrame = this.getCurrentFrame();
    this.setCurrentFrame(frame);

    EventProcessorProvider.getInstance().pushEvent(new TimelineHeadMovedEvent<>(
      this, event.getContext(), event.getFrame(),
      oldFrame, this.getCurrentFrame()
    ));
  }

  public TimelinePanel setCurrentScene(MutableCutscene cutscene) {
    this.currentScene = cutscene;
    return this;
  }

  public TimelinePanel setCurrentFrame(int currentFrame) {
    if (this.currentScene == null) return this;

    if (currentFrame < 0) currentFrame = 0;
    else if (currentFrame > this.currentScene.getLength()) currentFrame = this.currentScene.getLength();

    this.currentFrame = currentFrame;
    return this;
  }

  public MutableCutscene getCurrentScene() {
    return this.currentScene;
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

  public TimelineStyle getTimelineStyle() {
    return this.timelineStyle;
  }

  static {
    NvgRendererProvider.getInstance().addComponentRenderer(TimelinePanel.class, new TimelinePanelRenderer());
  }
}
