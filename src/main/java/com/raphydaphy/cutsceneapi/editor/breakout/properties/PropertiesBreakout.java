package com.raphydaphy.cutsceneapi.editor.breakout.properties;

import com.raphydaphy.breakoutapi.breakout.window.BreakoutWindow;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.cutscene.entity.particle.CutsceneParticleSource;
import com.raphydaphy.cutsceneapi.cutscene.track.MutableCutsceneTrack;
import com.raphydaphy.cutsceneapi.cutscene.track.keyframe.MutableTransformKeyframe;
import com.raphydaphy.cutsceneapi.cutscene.track.property.TransformProperty;
import com.raphydaphy.cutsceneapi.editor.CutsceneEditor;
import com.raphydaphy.cutsceneapi.editor.breakout.EditorBreakout;
import com.raphydaphy.cutsceneapi.editor.breakout.properties.component.FixedWidget;
import com.raphydaphy.cutsceneapi.entity.CutsceneCameraEntity;
import com.raphydaphy.shaded.org.joml.Vector2f;
import com.raphydaphy.shaded.org.joml.Vector3d;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.font.FontRegistry;
import org.liquidengine.legui.theme.Themes;
import org.liquidengine.legui.theme.colored.FlatColoredTheme;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;

import static org.liquidengine.legui.style.color.ColorUtil.fromInt;

public class PropertiesBreakout extends EditorBreakout {
  public static final Identifier IDENTIFIER = new Identifier(CutsceneAPI.MODID, "properties");

  private final CutsceneEditor editor;

  public PropertiesBreakout(CutsceneEditor editor) {
    super(IDENTIFIER, new BreakoutWindow("Cutscene Properties", 200, 200));
    this.editor = editor;

    this.window.setSize(400, this.client.getWindow().getHeight());
    this.window.setRelativePos(-420, 0);

    GLFW.glfwSetWindowSizeLimits(this.window.getHandle(), 400, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE, GLFW.GLFW_DONT_CARE);

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

    PropertiesGUI gui = (PropertiesGUI) this.gui;
    CutsceneCameraEntity camera = this.editor.getCamera();

    MutableCutscene currentScene = this.editor.getCurrentScene();
    if (currentScene != null) {
      gui.lengthInput.getInput().getTextState().setValue(currentScene.getLength());
      gui.framerateInput.getInput().getTextState().setValue(currentScene.getFramerate());
    }

    // TODO: clean up listeners

    gui.addObjectButton.getListenerMap().addListener(MouseClickEvent.class, (e) -> {
      if (e.getAction() == MouseClickEvent.MouseClickAction.CLICK) {
        this.createObject(gui.objectSelector.getSelection());
      }
    });

    gui.framerateInput.getInput().getListenerMap().addListener(FocusEvent.class, (event) -> {
      if (event.isFocused()) return;
      MutableCutscene cutscene = this.editor.getCurrentScene();
      int framerate = gui.framerateInput.getInput().getTextState().getValue();
      if (framerate != cutscene.getFramerate()) {
        if (framerate > 0) cutscene.setFramerate(framerate);
        else {
          CutsceneAPI.LOGGER.warn("Invalid cutscene framerate: " + framerate);
          gui.framerateInput.getInput().getTextState().setValue(cutscene.getFramerate());
        }
      }
    });

    gui.lengthInput.getInput().getListenerMap().addListener(FocusEvent.class, (event) -> {
      if (event.isFocused()) return;
      MutableCutscene cutscene = this.editor.getCurrentScene();
      int length = gui.lengthInput.getInput().getTextState().getValue();
      if (length != cutscene.getLength()) {
        if (length > 0) cutscene.setLength(length);
        else {
          CutsceneAPI.LOGGER.warn("Invalid cutscene length: " + length);
          gui.lengthInput.getInput().getTextState().setValue(cutscene.getLength());
        }
      }
    });

    gui.positionInput.addChangeListener(0, (e) -> camera.setPos(e.getNewValue(), camera.getY(), camera.getZ()));
    gui.positionInput.addChangeListener(1, (e) -> camera.setPos(camera.getX(), e.getNewValue(), camera.getZ()));
    gui.positionInput.addChangeListener(2, (e) -> camera.setPos(camera.getX(), camera.getY(), e.getNewValue()));

    gui.rotationInput.addChangeListener(0, (e) -> camera.pitch = e.getNewValue());
    gui.rotationInput.addChangeListener(1, (e) -> camera.yaw = e.getNewValue());

    gui.cameraKeyframeButton.getListenerMap().addListener(MouseClickEvent.class, (e) -> {
      if (e.getAction() != MouseClickEvent.MouseClickAction.CLICK) return;

      MutableCutscene cutscene = this.editor.getCurrentScene();
      if (cutscene == null) return;

      MutableCutsceneTrack<MutableTransformKeyframe> cameraTrack = cutscene.getCameraTrack();
      int frame = cutscene.getCurrentFrame();

      Vector3d keyframePos = new Vector3d(camera.getX(), camera.getY(), camera.getZ());
      Vector2f keyframeRot = new Vector2f(camera.pitch, camera.yaw);

      MutableTransformKeyframe existingKeyframe = cameraTrack.getKeyframe(frame);
      if (existingKeyframe != null) {
        existingKeyframe.getProperty().setPos(keyframePos).setRot(keyframeRot);
      } else {
        cameraTrack.setKeyframe(frame, new MutableTransformKeyframe(cameraTrack, frame, new TransformProperty(keyframePos, keyframeRot)));
      }
    });
  }

  @Override
  protected PropertiesGUI createGUI(int width, int height) {
    return new PropertiesGUI(this.getContext(), height);
  }

  public void update() {
    PropertiesGUI gui = (PropertiesGUI) this.gui;

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

  private void createObject(String type) {
    MutableCutscene cutscene = this.editor.getCurrentScene();
    if (cutscene == null) return;

    PropertiesGUI gui = (PropertiesGUI) this.gui;

    if (type.equals(PropertiesGUI.ObjectType.PARTICLE_SOURCE.getName())) {
      Vec3d cameraPos = this.editor.getCamera().getPos();
      Vector3d pos = new Vector3d(cameraPos.x, cameraPos.y, cameraPos.z);
      Vector3d velocityMultiplier = new Vector3d(1, 1, 1);

      CutsceneParticleSource source = new CutsceneParticleSource(this.editor.getParticleManager(), new Identifier("portal"), pos, velocityMultiplier);
      cutscene.addEntity(source);
      FixedWidget widget = gui.addParticleSource(source);

      widget.addWidgetCloseEventListener((e) -> cutscene.removeEntity(source));
    }
  }
}
