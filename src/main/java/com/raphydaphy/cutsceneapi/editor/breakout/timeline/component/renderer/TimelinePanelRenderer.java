package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.renderer;

import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelinePanel;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelinePanelHelper;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineStyle;
import com.raphydaphy.shaded.org.joml.Vector2f;
import com.raphydaphy.shaded.org.joml.Vector4f;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.input.Mouse;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.font.FontRegistry;
import org.liquidengine.legui.style.font.TextDirection;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.renderer.nvg.component.NvgDefaultComponentRenderer;
import org.liquidengine.legui.system.renderer.nvg.util.NvgShapes;
import org.liquidengine.legui.system.renderer.nvg.util.NvgText;

import static org.liquidengine.legui.style.util.StyleUtilities.getStyle;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.*;

public class TimelinePanelRenderer extends NvgDefaultComponentRenderer<TimelinePanel> {
  private static final int FRAMES_BETWEEN_MARKS = 5;
  private static final float FRAMES_BETWEEN_LARGE_MARKS = 10 * FRAMES_BETWEEN_MARKS;
  private static final float MARKER_LABEL_WIDTH = 60;
  private static final boolean DEBUG_RENDER = false;

  @Override
  public void renderSelf(TimelinePanel component, Context context, long nanovg) {
    createScissor(nanovg, component);
    {
      renderBackground(component, context, nanovg);

      MutableCutscene cutscene = component.getCurrentScene();
      if (cutscene != null) this.renderTimeline(cutscene, component, context, nanovg);
    }
    resetScissor(nanovg);
  }

  private void renderTimeline(MutableCutscene cutscene, TimelinePanel component, Context context, long nanovg) {
    Style style = component.getStyle();
    TimelineStyle timelineStyle = component.getTimelineStyle();

    float topHeight = timelineStyle.getTopHeight();
    float baselineSize = timelineStyle.getBaselineSize();

    Vector2f pos = component.getAbsolutePosition();
    Vector2f size = component.getSize();

    float frameWidth = TimelinePanelHelper.getFrameWidth(component);

    NvgShapes.drawRect(
      nanovg,
      new Vector2f(pos.x, pos.y + topHeight),
      new Vector2f(size.x, baselineSize),
      timelineStyle.getBaselineColor(), 0f
    );

    Vector2f mousePosition = Mouse.getCursorPosition();

    if (TimelinePanelHelper.isMouseOverComponent(component, mousePosition)) {
      float hoverX = mousePosition.x;

      if (TimelinePanelHelper.isMouseOverTop(component, mousePosition)) {
        int hoveredFrame = TimelinePanelHelper.getHoveredFrame(component, mousePosition);
        hoverX = Math.round(pos.x + hoveredFrame * frameWidth);
      }

      NvgShapes.drawRect(
        nanovg,
        new Vector2f(hoverX, pos.y),
        new Vector2f(1f, topHeight),
        timelineStyle.getHoveredMarkerColor(), 0f
      );
    }

    String font = getStyle(component, Style::getFont, FontRegistry.getDefaultFont());
    float fontSize = getStyle(component, Style::getFontSize, 16f);
    float headSize = timelineStyle.getHeadSize();

    int renderFrame = 0;
    while (renderFrame <= cutscene.getLength()) {

      float markerHeight = headSize;
      boolean largeMarker = renderFrame % FRAMES_BETWEEN_LARGE_MARKS == 0;

      if (!largeMarker) markerHeight /= 2;

      NvgShapes.drawRect(
        nanovg,
        new Vector2f(pos.x + renderFrame * frameWidth, pos.y + topHeight - markerHeight),
        new Vector2f(1f, markerHeight),
        timelineStyle.getMarkerColor(), 0f
      );

      if (largeMarker) {
        Vector4f rect = new Vector4f(
          pos.x + renderFrame * frameWidth - MARKER_LABEL_WIDTH / 2f,
          pos.y + topHeight - markerHeight - fontSize,
          MARKER_LABEL_WIDTH, fontSize
        );
        if (DEBUG_RENDER) NvgShapes.drawRect(nanovg, rect, ColorConstants.red(), 0);
        NvgText.drawTextLineToRect(
          nanovg, rect,
          false, HorizontalAlign.CENTER, VerticalAlign.MIDDLE, fontSize, font, Integer.toString(renderFrame),
          timelineStyle.getMarkerLabelColor(), TextDirection.HORIZONTAL
        );
      }

      renderFrame += FRAMES_BETWEEN_MARKS;
    }

    int currentFrame = component.getCurrentFrame();
    int headMarkerHeight = Math.round(headSize / 3f);
    int headX = Math.round(currentFrame * frameWidth);

    NvgShapes.drawRect(
      nanovg,
      new Vector2f(pos.x + headX - headSize / 2, pos.y + topHeight - headSize),
      new Vector2f(headSize, headSize),
      timelineStyle.getHeadColor(), new Vector4f(0, 0, 100, 100)
    );

    NvgShapes.drawRect(
      nanovg,
      new Vector2f(pos.x + headX, pos.y + topHeight - headMarkerHeight),
      new Vector2f(1, headMarkerHeight + baselineSize),
      timelineStyle.getMarkerColor(), 0
    );

    NvgShapes.drawRect(
      nanovg,
      new Vector2f(pos.x + headX, pos.y + topHeight + baselineSize),
      new Vector2f(1, size.y - topHeight - baselineSize),
      timelineStyle.getHeadColor(), 0
    );
  }
}
