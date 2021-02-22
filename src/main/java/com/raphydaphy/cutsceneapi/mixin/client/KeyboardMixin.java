package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

  @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
  public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo info) {
    if (CutsceneAPIClient.isEditorOpen()) {
      info.cancel();
    }
  }

  @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
  private void onChar(long window, int i, int j, CallbackInfo info) {
    if (CutsceneAPIClient.isEditorOpen()) {
      info.cancel();
    }
  }
}
