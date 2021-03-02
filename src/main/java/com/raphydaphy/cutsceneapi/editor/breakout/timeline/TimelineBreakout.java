package com.raphydaphy.cutsceneapi.editor.breakout.timeline;

import com.raphydaphy.breakoutapi.breakout.window.BreakoutWindow;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.CutsceneEditor;
import com.raphydaphy.cutsceneapi.editor.breakout.EditorBreakout;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

public class TimelineBreakout extends EditorBreakout {
  public static final Identifier IDENTIFIER = new Identifier(CutsceneAPI.MODID, "timeline");
  protected CutsceneEditor editor;

  public TimelineBreakout(CutsceneEditor editor) {
    super(IDENTIFIER, new BreakoutWindow("Cutscene Timeline", 200, 200));
    this.editor = editor;
    Window window = this.client.getWindow();
    this.window.setSize(window.getWidth(), 200);
    this.window.setRelativePos(0, window.getHeight() + 55);

    TimelineGUI gui = (TimelineGUI)this.gui;
    gui.setCurrentScene(this.editor.getCurrentScene());
  }

  @Override
  protected TimelineGUI createGUI(int width, int height) {
    return new TimelineGUI(width, height);
  }
}
