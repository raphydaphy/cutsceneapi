package com.raphydaphy.cutsceneapi.mixin.client;


import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At(value = "HEAD"), method = "renderHand", cancellable = true)
    private void renderHand(Camera camera_1, float float_1, CallbackInfo info) {
        if (CutsceneManager.hideHud(client.player)) {
            info.cancel();
        }
    }
}
