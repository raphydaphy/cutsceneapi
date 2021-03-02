package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.renderer;

import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.cutscene.track.CutsceneTrack;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineView;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper.TimelineViewHelper;
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

import java.util.List;

import static org.liquidengine.legui.style.util.StyleUtilities.getStyle;
import static org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils.*;

public class TimelineViewRenderer extends NvgDefaultComponentRenderer<TimelineView> {
  private static final int FRAMES_BETWEEN_MARKS = 5;
  private static final float FRAMES_BETWEEN_LARGE_MARKS = 10 * FRAMES_BETWEEN_MARKS;
  private static final float MARKER_LABEL_WIDTH = 60;
  private static final boolean DEBUG_RENDER = false;

  @Override
  public void renderSelf(TimelineView component, Context context, long nanovg) {
    createScissorByParent(nanovg, component);
    {
      renderBackground(component, context, nanovg);

      MutableCutscene cutscene = component.getTimeline().getCurrentScene();
      if (cutscene != null) this.renderTimeline(component, nanovg, cutscene);
    }
    resetScissor(nanovg);
  }

  private void renderTimeline(TimelineView component, long nanovg, MutableCutscene cutscene) {
    this.renderBaseline(component, nanovg);
    this.renderMarkers(component, nanovg, cutscene);
    this.renderTracks(component, nanovg, cutscene);
    this.renderHead(component, nanovg);
  }

  private void renderBaseline(TimelineView component, long nanovg) {
    TimelineStyle timelineStyle = component.getTimeline().getTimelineStyle();

    float topHeight = timelineStyle.getTopHeight();
    float baselineSize = timelineStyle.getBaselineSize();

    Vector2f pos = component.getOffsetPosition();
    Vector2f size = component.getScaledSize();

    NvgShapes.drawRect(
      nanovg,
      new Vector2f(pos.x, pos.y + topHeight),
      new Vector2f(size.x, baselineSize),
      timelineStyle.getBaselineColor(), 0f
    );
  }

  private void renderMarkers(TimelineView component, long nanovg, MutableCutscene cutscene) {
    TimelineStyle timelineStyle = component.getTimeline().getTimelineStyle();
    float topHeight = timelineStyle.getTopHeight();

    float frameWidth = TimelineViewHelper.getFrameWidth(component);

    Vector2f pos = component.getOffsetPosition();
    Vector2f mousePosition = Mouse.getCursorPosition();

    if (TimelineViewHelper.isMouseOverComponent(component, mousePosition)) {
      float hoverX = mousePosition.x;

      if (TimelineViewHelper.isMouseOverTop(component, mousePosition)) {
        int hoveredFrame = TimelineViewHelper.getHoveredFrame(component, mousePosition);
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
  }

  private void renderTracks(TimelineView component, long nanovg, MutableCutscene cutscene) {
    TimelineStyle timelineStyle = component.getTimeline().getTimelineStyle();

    List<CutsceneTrack> tracks = cutscene.getTracks();

    Vector2f pos = component.getOffsetPosition();
    Vector2f size = component.getScaledSize();

    float trackY = pos.y + timelineStyle.getTopHeight() + timelineStyle.getBaselineSize();

    for (CutsceneTrack track : tracks) {

      trackY += timelineStyle.getTrackHeight();
      NvgShapes.drawRect(
        nanovg,
        new Vector2f(pos.x, trackY),
        new Vector2f(size.x, timelineStyle.getTrackSeparatorSize()),
        timelineStyle.getTrackSeparatorColor(), 0
      );
      trackY += timelineStyle.getTrackSeparatorSize();
    }
  }

  private void renderHead(TimelineView component, long nanovg) {
    TimelineStyle timelineStyle = component.getTimeline().getTimelineStyle();

    Vector2f pos = component.getOffsetPosition();
    Vector2f size = component.getScaledSize();

    float topHeight = timelineStyle.getTopHeight();
    float headSize = timelineStyle.getHeadSize();
    float baselineSize = timelineStyle.getBaselineSize();

    float frameWidth = TimelineViewHelper.getFrameWidth(component);
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
