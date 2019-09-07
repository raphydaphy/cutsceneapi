package com.raphydaphy.cutsceneapi.mixin.client;


import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import com.raphydaphy.cutsceneapi.fakeworld.FakeWorldInteractionManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private GameMode gameMode;

    @Shadow
    private float currentBreakingProgress;
    @Shadow
    private boolean breakingBlock;
    @Shadow
    private int field_3716;
    @Shadow
    private BlockPos currentBreakingPos;
    @Shadow
    private float field_3713;
    @Shadow
    private ItemStack selectedStack;

    @Shadow
    protected abstract boolean isCurrentlyBreaking(BlockPos blockPos_1);

    @Shadow
    public abstract boolean breakBlock(BlockPos blockPos_1);

    @Inject(at = @At("HEAD"), method = "attackEntity", cancellable = true)
    private void attackEntity(PlayerEntity player, Entity entity, CallbackInfo info) {
        if (CutsceneManager.isActive(player)) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "breakBlock", cancellable = true)
    private void breakBlock(BlockPos blockPos_1, CallbackInfoReturnable<Boolean> info) {
        if (CutsceneManager.isActive(client.player)) {
            info.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "attackBlock", cancellable = true)
    private void attackBlock(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info) {
        if (client.world instanceof CutsceneWorld) {
            if (this.gameMode.shouldLimitWorldModification()) {
                if (this.gameMode == GameMode.SPECTATOR) {
                    info.setReturnValue(false);
                    return;
                } else if (!this.client.player.canModifyWorld()) {
                    ItemStack itemStack_1 = this.client.player.getMainHandStack();
                    if (itemStack_1.isEmpty()) {
                        info.setReturnValue(false);
                        return;
                    }
                    if (!itemStack_1.canDestroy(this.client.world.getTagManager(), new CachedBlockPosition(this.client.world, blockPos_1, false))) {
                        info.setReturnValue(false);
                        return;
                    }
                }
            }
            if (!this.client.world.getWorldBorder().contains(blockPos_1)) {
                info.setReturnValue(false);
            } else {
                if (this.gameMode.isCreative()) {
                    this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos_1, this.client.world.getBlockState(blockPos_1), 1.0F);
                    //this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos_1, direction_1));
                    ClientPlayerInteractionManager.method_2921(this.client, (ClientPlayerInteractionManager) (Object) this, blockPos_1, direction_1);
                    this.field_3716 = 5;
                } else if (!this.breakingBlock || !this.isCurrentlyBreaking(blockPos_1)) {
                    if (this.breakingBlock) {
                        //this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, direction_1));
                    }

                    BlockState blockState_1 = this.client.world.getBlockState(blockPos_1);
                    this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos_1, blockState_1, 0.0F);
                    //this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, blockPos_1, direction_1));
                    boolean boolean_1 = !blockState_1.isAir();

                    if (boolean_1 && this.currentBreakingProgress == 0.0F) {
                        blockState_1.onBlockBreakStart(this.client.world, blockPos_1, this.client.player);
                    }

                    if (boolean_1 && blockState_1.calcBlockBreakingDelta(this.client.player, this.client.player.world, blockPos_1) >= 1.0F) {
                        this.breakBlock(blockPos_1);
                    } else {
                        this.breakingBlock = true;
                        this.currentBreakingPos = blockPos_1;
                        this.selectedStack = this.client.player.getMainHandStack();
                        this.currentBreakingProgress = 0.0F;
                        this.field_3713 = 0.0F;
                        this.client.world.setBlockBreakingProgress(this.client.player.getEntityId(), this.currentBreakingPos, (int) (this.currentBreakingProgress * 10.0F) - 1);
                    }
                }

                info.setReturnValue(true);
            }
        } else if (CutsceneManager.isActive(client.player)) {
            info.setReturnValue(false);
        }
    }

    @Inject(at = @At("HEAD"), method = "interactBlock", cancellable = true)
    private void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info) {
        if (world instanceof CutsceneWorld) {
            info.setReturnValue(FakeWorldInteractionManager.interactBlock(player, (CutsceneWorld) world, hand, hitResult));
        } else if (CutsceneManager.isActive(client.player)) {
            info.setReturnValue(ActionResult.PASS);
        }
    }

    @Inject(at = @At("HEAD"), method = "interactItem", cancellable = true)
    private void interactItem(PlayerEntity player, World world, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        if (world instanceof CutsceneWorld) {
            info.setReturnValue(FakeWorldInteractionManager.interactItem(player, world, hand));
        } else if (CutsceneManager.isActive(client.player)) {
            info.setReturnValue(ActionResult.PASS);
        }
    }
}
