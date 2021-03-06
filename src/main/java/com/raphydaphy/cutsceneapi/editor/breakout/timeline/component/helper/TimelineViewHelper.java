package com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.helper;

import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.cutscene.track.MutableCutsceneTrack;
import com.raphydaphy.cutsceneapi.cutscene.track.keyframe.Keyframe;
import com.raphydaphy.cutsceneapi.cutscene.track.keyframe.MutableKeyframe;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.TimelineView;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.component.style.TimelineStyle;
import com.raphydaphy.shaded.org.joml.Vector2f;
import com.raphydaphy.shaded.org.joml.Vector2i;
import com.raphydaphy.shaded.org.joml.Vector4f;
import org.jetbrains.annotations.Nullable;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.system.renderer.nvg.util.NvgShapes;

import java.util.Set;

public class TimelineViewHelper {

  public static boolean isMouseOverComponent(Component timeline, Vector2f mousePosition) {
    return isMouseOverArea(timeline.getAbsolutePosition(), timeline.getSize(), mousePosition);
  }

  public static boolean isMouseOverArea(Vector2f pos, Vector2f size, Vector2f mousePosition) {
    return isMouseOverArea(new Vector4f(pos.x, pos.y, size.x, size.y), mousePosition);
  }

  public static boolean isMouseOverArea(Vector4f area, Vector2f mousePosition) {
    if (mousePosition.x < area.x || mousePosition.y < area.y) return false;
    if (mousePosition.x >= area.x + area.z || mousePosition.y >= area.y + area.w) return false;
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

  public static Vector4f getKeyframeArea(TimelineView component, int trackId, Keyframe keyframe) {
    TimelineStyle timelineStyle = component.getTimeline().getTimelineStyle();
    Vector2f pos = component.getOffsetPosition();

    float frameWidth = TimelineViewHelper.getFrameWidth(component);
    float trackHeight = timelineStyle.getTrackHeight();

    float trackY = pos.y + timelineStyle.getTopHeight() + timelineStyle.getBaselineSize() + trackHeight * trackId;

    Vector2f keyframeSize = timelineStyle.getKeyframeSize();

    return new Vector4f(
      pos.x + keyframe.getFrame() * frameWidth - keyframeSize.x / 2,
      trackY + trackHeight / 2 - keyframeSize.y / 2,
      keyframeSize.x, keyframeSize.y
    );
  }

  public static @Nullable MutableKeyframe getHoveredKeyframe(TimelineView component, Vector2f cursorPosition) {
    if (!isMouseOverComponent(component, cursorPosition)) return null;

    MutableCutscene cutscene = component.getTimeline().getCurrentScene();
    if (cutscene == null) return null;

    TimelineStyle timelineStyle = component.getTimeline().getTimelineStyle();
    Vector2f keyframeSize = timelineStyle.getKeyframeSize();

    Vector2i frameRange = getVisibleFrameRange(component);
    int maxKeyframesPerFrame = Math.max((int)Math.ceil(keyframeSize.x / getFrameWidth(component)), 1);

    frameRange.x = Math.max(frameRange.x - maxKeyframesPerFrame, 0);
    frameRange.y = Math.min(frameRange.y + maxKeyframesPerFrame, cutscene.getLength());

    for (int frame = frameRange.y; frame >= frameRange.x; frame--) {
      for (int trackId = 0; trackId < cutscene.getTracks().size(); trackId++) {
        MutableCutsceneTrack track = cutscene.getTracks().get(trackId);

        MutableKeyframe keyframe = track.getKeyframe(frame);
        if (keyframe == null) continue;

        Vector4f keyframeArea = getKeyframeArea(component, trackId, keyframe);
        if (isMouseOverArea(keyframeArea, cursorPosition)) return keyframe;
      }
    }

    return null;
  }
}
