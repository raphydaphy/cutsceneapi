package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import com.raphydaphy.cutsceneapi.hooks.MinecraftClientHooks;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin implements MinecraftClientHooks {
  @Shadow
  private boolean paused;

  @Override
  public void setPaused(boolean paused) {
    CutsceneAPI.LOGGER.info("Pausing forcefully");
    this.paused = paused;
  }

  @Inject(method = "openPauseMenu", at = @At("HEAD"), cancellable = true)
  public void openPauseMenu(boolean pause, CallbackInfo info) {
    if (CutsceneAPIClient.isEditorOpen()) {
      info.cancel();
    }
  }

  @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
  public void tick(CallbackInfo info) {
    if (CutsceneAPIClient.isEditorOpen()) {
      CutsceneAPIClient.EDITOR.update();
      info.cancel();
    }
  }
}
