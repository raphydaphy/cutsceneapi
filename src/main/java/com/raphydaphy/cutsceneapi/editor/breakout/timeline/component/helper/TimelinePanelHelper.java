package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelinePanel;
import com.raphydaphy.shaded.org.joml.Vector2f;

public class TimelinePanelHelper {

  public static boolean isMouseOverTop(TimelinePanel timeline, Vector2f mousePosition) {
    if (!isMouseOver(timeline, mousePosition)) return false;
    return mousePosition.y < timeline.getAbsolutePosition().y + timeline.getTimelineStyle().getTopHeight();
  }

  public static boolean isMouseOver(TimelinePanel timeline, Vector2f mousePosition) {
    Vector2f pos = timeline.getAbsolutePosition();
    Vector2f size = timeline.getSize();

    if (mousePosition.x < pos.x || mousePosition.y < pos.y) return false;
    if (mousePosition.x >= pos.x + size.x || mousePosition.y >= pos.y + size.y) return false;

    return true;
  }

  public static int getHoveredFrame(TimelinePanel panel, Vector2f mousePosition) {
    MutableCutscene cutscene = panel.getCurrentScene();
    if (cutscene == null) return 0;

    Vector2f pos = panel.getAbsolutePosition();
    float headSize = panel.getTimelineStyle().getHeadSize();

    float min = 0;
    float max = min + cutscene.getLength();
    float difference = max - min;

    float percentage = (mousePosition.x - pos.x - headSize / 2f) / (panel.getSize().x - headSize);
    float value = difference * percentage + min;

    if (value < 0) value = 0;
    else if (value > cutscene.getLength()) return cutscene.getLength();

    return Math.round(value);
  }
}
