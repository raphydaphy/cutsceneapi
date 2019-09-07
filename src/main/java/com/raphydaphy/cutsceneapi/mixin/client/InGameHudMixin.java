package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(at = @At(value = "HEAD"), method = "render", cancellable = true)
    private void renderhudPre(float partialTicks, CallbackInfo info) {
        if (CutsceneManager.hideHud(client.player)) {
            CutsceneManager.renderHud();
            info.cancel();
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "render")
    private void renderHudPost(float partialTicks, CallbackInfo info) {
        if (CutsceneManager.isActive(client.player)) {
            CutsceneManager.renderHud();
        }
    }
}
