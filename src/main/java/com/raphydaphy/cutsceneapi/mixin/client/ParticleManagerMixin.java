package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
  @Inject( method = "renderParticles", at = @At("HEAD"))
  public void renderParticles(MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, LightmapTextureManager lightmapTextureManager, Camera camera, float f, CallbackInfo info) {
    if (CutsceneAPIClient.isEditorOpen()) {
      CutsceneAPIClient.EDITOR.getParticleManager().renderParticles(matrixStack, immediate, lightmapTextureManager, camera, f);
    }
  }
}
