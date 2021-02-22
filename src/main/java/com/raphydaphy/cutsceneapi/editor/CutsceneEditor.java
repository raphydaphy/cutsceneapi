package com.raphydaphy.cutsceneapi.editor;


import com.raphydaphy.breakoutapi.BreakoutAPIClient;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.editor.breakout.properties.PropertiesBreakout;
import com.raphydaphy.cutsceneapi.editor.breakout.timeline.TimelineBreakout;
import com.raphydaphy.cutsceneapi.editor.input.MouseTracker;
import com.raphydaphy.cutsceneapi.entity.CutsceneCameraEntity;
import com.raphydaphy.cutsceneapi.hooks.GameRendererHooks;
import com.raphydaphy.cutsceneapi.hooks.MinecraftClientHooks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.MovementType;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class CutsceneEditor {
  private static final Identifier PROPERTIES = new Identifier(CutsceneAPI.MODID, "properties");
  private static final Identifier TIMELINE = new Identifier(CutsceneAPI.MODID, "timeline");

  private MinecraftClient client;
  private CutsceneCameraEntity camera;
  private MouseTracker mouseTracker;

  public CutsceneEditor() throws IllegalStateException {
    this.client = MinecraftClient.getInstance();

    if (this.client.player == null || this.client.world == null) {
      throw new IllegalStateException("Tried to open the cutscene editor before entering a world");
    }

    ((MinecraftClientHooks)this.client).setPaused(true);
    this.client.mouse.unlockCursor();

    ((GameRendererHooks)this.client.gameRenderer).setRenderHand(false);

    assert this.client.player != null;
    this.camera = new CutsceneCameraEntity(this.client.world, this.client.player.getX(), this.client.player.getY(), this.client.player.getZ());
    this.client.setCameraEntity(this.camera);

    this.mouseTracker = new MouseTracker(this.client.mouse.getX(), this.client.mouse.getY());

    BreakoutAPIClient.openBreakout(PROPERTIES, new PropertiesBreakout(PROPERTIES));
    BreakoutAPIClient.openBreakout(TIMELINE, new TimelineBreakout(TIMELINE));
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
  }

  public MouseTracker getMouseTracker() {
    return this.mouseTracker;
  }

  public void close() {
    this.client.setCameraEntity(this.client.player);

    ((GameRendererHooks)this.client.gameRenderer).setRenderHand(true);

    BreakoutAPIClient.closeBreakout(PROPERTIES);
    BreakoutAPIClient.closeBreakout(TIMELINE);
  }

}
