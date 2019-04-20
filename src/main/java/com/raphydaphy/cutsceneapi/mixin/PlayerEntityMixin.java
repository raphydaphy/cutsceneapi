package com.raphydaphy.cutsceneapi.mixin;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        if (CutsceneManager.isActive((PlayerEntity) (Object) this)) {
            this.onGround = false;
            if (world.isClient) {
                cutsceneAPIClientTick();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "interact", cancellable = true)
    private void interact(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (CutsceneManager.isActive((PlayerEntity) (Object) this)) {
            info.setReturnValue(ActionResult.PASS);
        }
    }

    private void cutsceneAPIClientTick() {
        CutsceneManager.updateClient();
    }
}
