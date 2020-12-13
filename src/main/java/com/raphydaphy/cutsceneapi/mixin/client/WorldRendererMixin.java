package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow
    private ClientWorld world;

    @Inject(at = @At("HEAD"), method = "drawShapeOutline", cancellable = true)
    private static void drawShapeOutline(MatrixStack matrixStack_1,
                                         VertexConsumer vertexConsumer_1,
                                         VoxelShape voxelShape_1,
                                         double double_1,
                                         double double_2,
                                         double double_3,
                                         float float_1,
                                         float float_2,
                                         float float_3,
                                         float float_4, CallbackInfo info) {
        if (CutsceneManager.hideHud(MinecraftClient.getInstance().player)) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "renderEntity", cancellable = true)
    private void renderEntities(Entity entity_1,
                                double double_1,
                                double double_2,
                                double double_3,
                                float float_1,
                                MatrixStack matrixStack_1,
                                VertexConsumerProvider vertexConsumerP, CallbackInfo info) {
        if (world instanceof CutsceneWorld) {
            info.cancel();
        }
    }
}
