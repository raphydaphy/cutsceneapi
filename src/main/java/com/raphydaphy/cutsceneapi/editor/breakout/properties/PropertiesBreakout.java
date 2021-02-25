package com.raphydaphy.cutsceneapi.editor.breakout.properties;

import com.raphydaphy.breakoutapi.BreakoutAPI;
import com.raphydaphy.breakoutapi.breakout.window.BreakoutWindow;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.editor.CutsceneEditor;
import com.raphydaphy.cutsceneapi.editor.breakout.EditorBreakout;
import com.raphydaphy.cutsceneapi.entity.CutsceneCameraEntity;
import net.minecraft.util.Identifier;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.font.FontRegistry;
import org.liquidengine.legui.theme.Themes;
import org.liquidengine.legui.theme.colored.FlatColoredTheme;

import java.text.DecimalFormat;

import static org.liquidengine.legui.style.color.ColorUtil.fromInt;

public class PropertiesBreakout extends EditorBreakout {
  public static final Identifier IDENTIFIER = new Identifier(CutsceneAPI.MODID, "properties");

  private CutsceneEditor editor;

  public PropertiesBreakout(CutsceneEditor editor) {
    super(IDENTIFIER, new BreakoutWindow("Cutscene Properties", 200, 200));
    this.editor = editor;

    this.window.setSize(400, this.client.getWindow().getHeight());
    this.window.setRelativePos(-420, 0);

    Themes.setDefaultTheme(new FlatColoredTheme(
      fromInt(245, 245, 245, 1), // backgroundColor
      fromInt(176, 190, 197, 1), // borderColor
      fromInt(176, 190, 197, 1), // sliderColor
      fromInt(100, 181, 246, 1), // strokeColor
      fromInt(165, 214, 167, 1), // allowColor
      fromInt(239, 154, 154, 1), // denyColor
      ColorConstants.transparent(), // shadowColor
      ColorConstants.darkGray(), // text color
      FontRegistry.getDefaultFont(), // font
      16f //font size
    ));

    PropertiesGUI gui = (PropertiesGUI)this.gui;
    CutsceneCameraEntity camera = this.editor.getCamera();

    // TODO: clean up listeners

    gui.positionInput.getField(0).addTextInputContentChangeEventListener((event) -> {
      try {
        double value = Double.parseDouble(event.getNewValue());
        this.editor.getCamera().setPos(value, camera.getY(), camera.getZ());
      } catch (NumberFormatException e) {
        BreakoutAPI.LOGGER.warn("Invalid property input:" + event.getNewValue());
      }
    });

    gui.positionInput.getField(1).addTextInputContentChangeEventListener((event) -> {
      try {
        double value = Double.parseDouble(event.getNewValue());
        camera.setPos(camera.getX(), value, camera.getZ());
      } catch (NumberFormatException e) {
        BreakoutAPI.LOGGER.warn("Invalid property input:" + event.getNewValue());
      }
    });

    gui.positionInput.getField(2).addTextInputContentChangeEventListener((event) -> {
      try {
        double value = Double.parseDouble(event.getNewValue());
        camera.setPos(camera.getX(), camera.getY(), value);
      } catch (NumberFormatException e) {
        BreakoutAPI.LOGGER.warn("Invalid property input:" + event.getNewValue());
      }
    });

    gui.rotationInput.getField(0).addTextInputContentChangeEventListener((event) -> {
      try {
        camera.pitch = Float.parseFloat(event.getNewValue());
      } catch (NumberFormatException e) {
        BreakoutAPI.LOGGER.warn("Invalid property input:" + event.getNewValue());
      }
    });

    gui.rotationInput.getField(1).addTextInputContentChangeEventListener((event) -> {
      try {
        camera.yaw = Float.parseFloat(event.getNewValue());
      } catch (NumberFormatException e) {
        BreakoutAPI.LOGGER.warn("Invalid property input:" + event.getNewValue());
      }
    });
  }

  @Override
  protected PropertiesGUI createGUI(int width, int height) {
    return new PropertiesGUI(height);
  }

  public void update() {
    PropertiesGUI gui = (PropertiesGUI)this.gui;

    CutsceneCameraEntity camera = this.editor.getCamera();
    DecimalFormat df = new DecimalFormat("#.##");

    if (!gui.positionInput.isFocussed()) {
      gui.positionInput.getField(0).getTextState().setText(df.format(camera.getX()));
      gui.positionInput.getField(1).getTextState().setText(df.format(camera.getY()));
      gui.positionInput.getField(2).getTextState().setText(df.format(camera.getZ()));
    }

    if (!gui.rotationInput.isFocussed()) {
      gui.rotationInput.getField(0).getTextState().setText(df.format(camera.pitch));
      gui.rotationInput.getField(1).getTextState().setText(df.format(camera.yaw));
    }
  }
}
