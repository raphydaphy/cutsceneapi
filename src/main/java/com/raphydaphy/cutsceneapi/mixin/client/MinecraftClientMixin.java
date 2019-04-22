package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.cutscene.DefaultClientCutscene;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    public ClientPlayerEntity player;

    @Shadow
    public ClientWorld world;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void constructor(RunArgs args, CallbackInfo info) {
        CutsceneAPI.REALWORLD_CUTSCENE = new DefaultClientCutscene(CutsceneAPI.REALWORLD_CUTSCENE.getLength());
        CutsceneAPI.FAKEWORLD_CUTSCENE_1 = new DefaultClientCutscene(CutsceneAPI.FAKEWORLD_CUTSCENE_1.getLength());
        CutsceneAPI.FAKEWORLD_CUTSCENE_2 = new DefaultClientCutscene(CutsceneAPI.FAKEWORLD_CUTSCENE_2.getLength());
        CutsceneAPI.VOIDWORLD_CUTSCENE = new DefaultClientCutscene(CutsceneAPI.VOIDWORLD_CUTSCENE.getLength());
        CutsceneAPI.DRAGONSTONE_CUTSCENE = new DefaultClientCutscene(CutsceneAPI.DRAGONSTONE_CUTSCENE.getLength());
    }

    @Inject(at = @At(value = "INVOKE_STRING", args = "ldc=gameRenderer", target = "Lnet/minecraft/util/profiler/DisableableProfiler;swap(Ljava/lang/String;)V"), method = "render")
    private void worldRenderTick(boolean renderWorldIn, CallbackInfo info) {
        if (world != null) {
            if (CutsceneManager.isActive(player)) {
                CutsceneManager.updateLook();
            }
        }
    }
}
