package com.raphydaphy.cutsceneapi.editor;


import com.raphydaphy.breakoutapi.BreakoutAPI;
import com.raphydaphy.breakoutapi.BreakoutAPIClient;
import com.raphydaphy.cutsceneapi.cutscene.MutableCutscene;
import com.raphydaphy.cutsceneapi.cutscene.entity.particle.CutsceneParticleManager;
import com.raphydaphy.cutsceneapi.cutscene.track.MutableCutsceneTrack;
import com.raphydaphy.cutsceneapi.cutscene.track.keyframe.MutableTransformKeyframe;
import com.raphydaphy.cutsceneapi.cutscene.track.property.TransformProperty;
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

  private final MinecraftClient client;
  private final CutsceneCameraEntity camera;
  private final MouseTracker mouseTracker;
  private final CutsceneParticleManager particleManager;

  private MutableCutscene currentScene;

  public CutsceneEditor() throws IllegalStateException {
    this.client = MinecraftClient.getInstance();

    if (this.client.player == null || this.client.world == null) {
      throw new IllegalStateException("Tried to open the cutscene editor before entering a world");
    }

    ((MinecraftClientHooks) this.client).setPaused(true);
    ((GameRendererHooks) this.client.gameRenderer).setRenderHand(false);
    this.client.mouse.unlockCursor();

    this.currentScene = new MutableCutscene();

    assert this.client.player != null;
    this.camera = new CutsceneCameraEntity(this.client.world, this.client.player.getX(), this.client.player.getY(), this.client.player.getZ());
    this.client.setCameraEntity(this.camera);

    this.mouseTracker = new MouseTracker(this.client.mouse.getX(), this.client.mouse.getY());
    this.particleManager = new CutsceneParticleManager(this.client.particleManager, this.client.world, this.client.getTextureManager());

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

  // movementInput = (left, up, forward)
  private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
    double d = movementInput.lengthSquared();
    if (d < 1.0E-7D) {
      return Vec3d.ZERO;
    } else {
      Vec3d vec3d = (d > 1.0D ? movementInput.normalize() : movementInput).multiply(speed);
      float f = MathHelper.sin(yaw * 0.017453292F);
      float g = MathHelper.cos(yaw * 0.017453292F);
      return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
    }
  }

  public void update() {
    float orbitSpeed = 0.2f;
    if (this.mouseTracker.isLeftButtonDown()) {
      Vec3d movement = new Vec3d(this.mouseTracker.getCursorDeltaX() * 1000, this.mouseTracker.getCursorDeltaY() * 1000, 0);
      Vec3d velocity = movementInputToVelocity(movement, 1f, this.camera.yaw);

      this.camera.move(MovementType.SELF, velocity.multiply(1 / 5f));
    }

    if (this.mouseTracker.isRightButtonDown()) {
      this.camera.yaw = MathHelper.wrapDegrees(this.camera.yaw - (float) this.mouseTracker.getCursorDeltaX() * orbitSpeed);
      this.camera.pitch = MathHelper.wrapDegrees(this.camera.pitch - (float) this.mouseTracker.getCursorDeltaY() * orbitSpeed);
    }

    if (this.mouseTracker.getVerticalScrollDelta() != 0) {
      Vec3d vec = this.camera.getRotationVec(this.client.getTickDelta()).multiply(this.mouseTracker.getVerticalScrollDelta());
      this.camera.move(MovementType.SELF, vec);
    }

    this.camera.update();
    this.mouseTracker.update();
    this.particleManager.tick();

    if (this.propertiesBreakout != null) this.propertiesBreakout.update();
  }

  public void startFrame() {
    if (this.currentScene == null) return;
    this.currentScene.update();

    int currentFrame = this.currentScene.getCurrentFrame();
    if (currentFrame != this.currentScene.getPreviousFrame()) {
      this.onFrameChanged(currentFrame);
    }

    this.currentScene.updateDelta();
  }

  private void onFrameChanged(int currentFrame) {
    MutableCutsceneTrack<MutableTransformKeyframe> cameraTrack = this.currentScene.getCameraTrack();

    MutableTransformKeyframe prevKeyframe = cameraTrack.getPrevKeyframe(currentFrame);
    if (prevKeyframe == null) return;

    MutableTransformKeyframe nextKeyframe = cameraTrack.getNextKeyframe(currentFrame);
    if (nextKeyframe == null) {
      this.camera.setTransform(prevKeyframe.getProperty());
      return;
    }

    int diff = nextKeyframe.getFrame() - prevKeyframe.getFrame();
    int progress = currentFrame - prevKeyframe.getFrame();
    float delta = (float) progress / diff;

    TransformProperty interp = prevKeyframe.interpolate(nextKeyframe, delta);
    this.camera.setTransform(interp);
  }

  public MouseTracker getMouseTracker() {
    return this.mouseTracker;
  }

  public CutsceneParticleManager getParticleManager() {
    return this.particleManager;
  }

  public CutsceneCameraEntity getCamera() {
    return this.camera;
  }

  public MutableCutscene getCurrentScene() {
    return this.currentScene;
  }

  public void close() {
    this.client.setCameraEntity(this.client.player);

    ((GameRendererHooks) this.client.gameRenderer).setRenderHand(true);
    this.client.getWindow().setFramerateLimit(this.client.options.maxFps);

    BreakoutAPIClient.closeBreakout(PropertiesBreakout.IDENTIFIER);
    BreakoutAPIClient.closeBreakout(TimelineBreakout.IDENTIFIER);
  }

}
