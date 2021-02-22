package com.raphydaphy.cutsceneapi.editor.breakout.timeline;

import com.raphydaphy.breakoutapi.breakout.window.BreakoutWindow;
import com.raphydaphy.cutsceneapi.editor.breakout.EditorBreakout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class TimelineBreakout extends EditorBreakout {

  public TimelineBreakout(Identifier identifier) {
    super(identifier, new BreakoutWindow("Cutscene Timeline", MinecraftClient.getInstance().getWindow().getWidth(), 200));
  }

  @Override
  protected TimelineGUI createGUI(int width, int height) {
    return new TimelineGUI(width, height);
  }
}
