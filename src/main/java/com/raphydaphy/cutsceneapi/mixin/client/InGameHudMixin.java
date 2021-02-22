package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void render(MatrixStack matrices, float tickDelta, CallbackInfo info) {
    if (CutsceneAPIClient.isEditorOpen()) {
      info.cancel();
    }
  }
}
