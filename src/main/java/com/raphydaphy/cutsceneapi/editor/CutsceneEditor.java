package com.raphydaphy.cutsceneapi.editor;


import com.raphydaphy.breakoutapi.BreakoutAPI;
import com.raphydaphy.breakoutapi.BreakoutAPIClient;
import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.editor.breakout.properties.PropertiesBreakout;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.TimelineBreakout;
import com.raphydaphy.cutsceneapi.editor.input.MouseTracker;
import com.raphydaphy.cutsceneapi.entity.CutsceneCameraEntity;
import com.raphydaphy.cutsceneapi.hooks.GameRendererHooks;
import com.raphydaphy.cutsceneapi.hooks.MinecraftClientHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CutsceneEditor {
  private PropertiesBreakout propertiesBreakout;
  private TimelineBreakout timelineBreakout;

  private MinecraftClient client;
  private CutsceneCameraEntity camera;
  private MouseTracker mouseTracker;

  private MutableCutscene currentScene;

  public CutsceneEditor() throws IllegalStateException {
    this.client = MinecraftClient.getInstance();

    if (this.client.player == null || this.client.world == null) {
      throw new IllegalStateException("Tried to open the cutscene editor before entering a world");
    }

    ((MinecraftClientHooks)this.client).setPaused(true);
    ((GameRendererHooks)this.client.gameRenderer).setRenderHand(false);
    this.client.mouse.unlockCursor();

    this.currentScene = new MutableCutscene();

    assert this.client.player != null;
    this.camera = new CutsceneCameraEntity(this.client.world, this.client.player.getX(), this.client.player.getY(), this.client.player.getZ());
    this.client.setCameraEntity(this.camera);

    this.mouseTracker = new MouseTracker(this.client.mouse.getX(), this.client.mouse.getY());

    this.propertiesBreakout = new PropertiesBreakout(this);
    BreakoutAPIClient.openBreakout(this.propertiesBreakout);

    new Thread(() -> {
      try {
        Thread.sleep(1000);
        this.client.execute(() -> {
          this.timelineBreakout = new TimelineBreakout(this);
          BreakoutAPIClient.openBreakout(this.timelineBreakout);
        });
      } catch (InterruptedException e) {
        BreakoutAPI.LOGGER.warn("Failed to open cutscene timeline with delay", e);
      }
    }).start();
  }

  public void update() {
    float orbitSpeed = 0.2f;
    if (this.mouseTracker.isRightButtonDown()) {
      this.camera.yaw = MathHelper.wrapDegrees(this.camera.yaw - (float)this.mouseTracker.getCursorDeltaX() * orbitSpeed);
      this.camera.pitch = MathHelper.wrapDegrees(this.camera.pitch - (float) this.mouseTracker.getCursorDeltaY() * orbitSpeed);
    }

    if (this.mouseTracker.getVerticalScrollDelta() != 0) {
      Vec3d vec = this.camera.getRotationVec(this.client.getTickDelta()).multiply(this.mouseTracker.getVerticalScrollDelta());
      this.camera.move(MovementType.SELF, vec);
    }

    this.camera.update();
    this.mouseTracker.update();

    if (this.propertiesBreakout != null) this.propertiesBreakout.update();
  }

  public MouseTracker getMouseTracker() {
    return this.mouseTracker;
  }

  public CutsceneCameraEntity getCamera() {
    return this.camera;
  }

  public MutableCutscene getCurrentScene() {
    return this.currentScene;
  }

  public void close() {
    this.client.setCameraEntity(this.client.player);

    ((GameRendererHooks)this.client.gameRenderer).setRenderHand(true);
    this.client.getWindow().setFramerateLimit(this.client.options.maxFps);

    BreakoutAPIClient.closeBreakout(PropertiesBreakout.IDENTIFIER);
    BreakoutAPIClient.closeBreakout(TimelineBreakout.IDENTIFIER);
  }

}
