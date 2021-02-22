package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

  @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
  private void onMouseButton(long window, int button, int action, int mods, CallbackInfo info) {
    if (CutsceneAPIClient.isEditorOpen()) {
      CutsceneAPIClient.EDITOR.getMouseTracker().onMouseButton(window, button, action, mods);
      info.cancel();
    }
  }

  @Inject(method = "onCursorPos", at = @At("HEAD"), cancellable = true)
  private void onCursorPos(long window, double x, double y, CallbackInfo info) {
    if (CutsceneAPIClient.isEditorOpen()) {
      CutsceneAPIClient.EDITOR.getMouseTracker().onCursorPos(window, x, y);
      info.cancel();
    }
  }

  @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
  private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
    if (CutsceneAPIClient.isEditorOpen()) {
      CutsceneAPIClient.EDITOR.getMouseTracker().onMouseScroll(window, horizontal, vertical);
      info.cancel();
    }
  }

  @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
  public void lockCursor(CallbackInfo info) {
    if (CutsceneAPIClient.isEditorOpen()) info.cancel();
  }
}
