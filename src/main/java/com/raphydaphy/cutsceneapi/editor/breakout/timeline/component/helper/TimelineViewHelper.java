package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper;

import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineView;
import com.raphydaphy.shaded.org.joml.Vector2f;
import com.raphydaphy.shaded.org.joml.Vector2i;
import org.liquidengine.legui.component.Component;

public class TimelineViewHelper {

  public static boolean isMouseOverComponent(Component timeline, Vector2f mousePosition) {
    Vector2f pos = timeline.getAbsolutePosition();
    Vector2f size = timeline.getSize();

    if (mousePosition.x < pos.x || mousePosition.y < pos.y) return false;
    if (mousePosition.x >= pos.x + size.x || mousePosition.y >= pos.y + size.y) return false;

    return true;
  }

  public static boolean isMouseOverTop(TimelineView timeline, Vector2f mousePosition) {
    if (!isMouseOverComponent(timeline, mousePosition)) return false;
    return mousePosition.y < timeline.getAbsolutePosition().y + timeline.getTimeline().getTimelineStyle().getTopHeight();
  }

  public static int getHoveredFrame(TimelineView panel, Vector2f mousePosition) {
    MutableCutscene cutscene = panel.getTimeline().getCurrentScene();
    if (cutscene == null) return 0;

    Vector2f offsetPos = panel.getOffsetPosition();
    float headSize = panel.getTimeline().getTimelineStyle().getHeadSize();
    int length = cutscene.getLength();

    float percentage = (mousePosition.x - offsetPos.x - headSize / 2f) / (panel.getScaledSize().x - headSize);
    float value = length * percentage;

    if (value < 0) value = 0;
    else if (value > cutscene.getLength()) return cutscene.getLength();

    return Math.round(value);
  }

  public static float getFrameWidth(TimelineView panel) {
    MutableCutscene cutscene = panel.getTimeline().getCurrentScene();
    if (cutscene == null) return 0;

    return panel.getScaledSize().x / cutscene.getLength();
  }

  public static Vector2i getVisibleFrameRange(TimelineView panel) {
    MutableCutscene cutscene = panel.getTimeline().getCurrentScene();
    if (cutscene == null) return new Vector2i(0, 0);

    float lengthBefore = panel.getAbsolutePosition().x - panel.getOffsetPosition().x;
    float lengthAfter = panel.getScaledSize().x - panel.getSize().x - lengthBefore;
    float frameWidth = getFrameWidth(panel);

    int firstFrame = (int)Math.ceil(lengthBefore / frameWidth);
    int lastFrame = cutscene.getLength() - (int)Math.ceil(lengthAfter / frameWidth);

    return new Vector2i(firstFrame, lastFrame);
  }

  public static String formatFrameTime(int totalFrames, int framerate) {
    if (totalFrames == 0) return "00:00:00:00";
    else if (framerate == 0) return Integer.toString(totalFrames);

    int frames = totalFrames % framerate;
    int seconds = totalFrames / framerate;
    int minutes = seconds / 60;
    int hours = minutes / 60;

    return String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, frames);
  }
}
