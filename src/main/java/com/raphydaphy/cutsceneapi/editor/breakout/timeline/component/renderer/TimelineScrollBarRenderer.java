package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.renderer;

import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineScrollBar;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineScrollBarHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineScrollBarStyle;
import com.raphydaphy.shaded.org.joml.Vector2f;
import org.liquidengine.legui.input.Mouse;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.renderer.nvg.component.NvgDefaultComponentRenderer;
import org.liquidengine.legui.system.renderer.nvg.util.NvgShapes;

import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.createScissor;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.resetScissor;

public class TimelineScrollBarRenderer extends NvgDefaultComponentRenderer<TimelineScrollBar> {
  @Override
  public void renderSelf(TimelineScrollBar component, Context context, long nanovg) {
    createScissor(nanovg, component);
    {
      renderBackground(component, context, nanovg);
      this.renderTimelineScrollBar(component, context, nanovg);
    }
    resetScissor(nanovg);
  }

  private void renderTimelineScrollBar(TimelineScrollBar component, Context context, long nanovg) {
    Vector2f pos = component.getAbsolutePosition();
    Vector2f size = component.getSize();

    Style style = component.getStyle();
    TimelineScrollBarStyle scrollBarStyle = component.getScrollBarStyle();

    float leftPercent = component.getLeftPercent();
    float rightPercent = component.getRightPercent();

    float handleSize = size.y;
    float halfHandleSize = handleSize / 2f;

    float leftPos = pos.x + halfHandleSize + (size.x - handleSize) * leftPercent;
    float rightPos = pos.x + halfHandleSize + (size.x - handleSize) * rightPercent;
    float barLength = rightPos - leftPos;

    NvgShapes.drawRect(
      nanovg,
      new Vector2f(leftPos, pos.y),
      new Vector2f(barLength, size.y),
      scrollBarStyle.getBarColor(), 5
    );

    this.drawHandle(component, nanovg, leftPercent);
    this.drawHandle(component, nanovg, rightPercent);
  }

  private void drawHandle(TimelineScrollBar component, long nanovg, float percent) {
    Vector2f pos = component.getAbsolutePosition();
    Vector2f size = component.getSize();

    float handleSize = size.y;
    float halfHandleSize = handleSize / 2f;

    TimelineScrollBarStyle scrollBarStyle = component.getScrollBarStyle();
    float handlePos = pos.x + halfHandleSize + (size.x - handleSize) * percent;

    Vector2f mousePosition = Mouse.getCursorPosition();
    boolean mouseOver = TimelineScrollBarHelper.isMouseOverHandle(component, mousePosition, percent);

    NvgShapes.drawRect(
      nanovg,
      new Vector2f(handlePos - halfHandleSize, pos.y),
      new Vector2f(handleSize, handleSize),
      scrollBarStyle.getBarColor(), 100
    );

    NvgShapes.drawRectStroke(
      nanovg,
      new Vector2f(handlePos - halfHandleSize, pos.y),
      new Vector2f(handleSize, handleSize),
      mouseOver ? scrollBarStyle.getHandleHoverColor() : scrollBarStyle.getHandleColor(),
      2f, 100
    );
  }
}
