package com.raphydaphy.cutsceneapi.editor.breakout;

import com.raphydaphy.breakoutapi.breakout.GUIBreakout;
import com.raphydaphy.breakoutapi.breakout.window.BreakoutWindow;
import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.event.WindowSizeEvent;
import org.liquidengine.legui.listener.WindowSizeEventListener;

public abstract class EditorBreakout extends GUIBreakout {
  protected Panel gui;

  public EditorBreakout(Identifier identifier, BreakoutWindow window) {
    super(identifier, window);
    this.getWindow().keeper.getChainWindowCloseCallback().add(this::onClosed);
  }

  protected abstract Panel createGUI(int width, int height);

  @Override
  protected void createGuiElements(int width, int height) {
    this.gui = this.createGUI(width, height);
    this.gui.setFocusable(false);
    this.gui.getListenerMap().addListener(WindowSizeEvent.class, (WindowSizeEventListener) event -> this.gui.setSize(event.getWidth(), event.getHeight()));
    this.frame.getContainer().add(this.gui);
  }

  private void onClosed(long window) {
    if (window == this.getWindow().getHandle() && CutsceneAPIClient.EDITOR != null) {
      CutsceneAPIClient.EDITOR.close();
      CutsceneAPIClient.EDITOR = null;
    }
  }
}