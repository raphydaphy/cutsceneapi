package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.hooks.GameRendererHooks;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRenderer.class)
public class GameRendererMixin implements GameRendererHooks {
  @Shadow
  private boolean renderHand;

  @Override
  public void setRenderHand(boolean renderHand) {
    this.renderHand = renderHand;
  }
}
