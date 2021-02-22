package com.raphydaphy.cutsceneapi.editor.breakout.properties;

import com.raphydaphy.breakoutapi.breakout.window.BreakoutWindow;
import com.raphydaphy.cutsceneapi.editor.breakout.EditorBreakout;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class PropertiesBreakout extends EditorBreakout {

  public PropertiesBreakout(Identifier identifier) {
    super(identifier, new BreakoutWindow("Cutscene Properties", 200, MinecraftClient.getInstance().getWindow().getHeight()));
  }

  @Override
  protected PropertiesGUI createGUI(int width, int height) {
    return new PropertiesGUI(width, height);
  }
}
