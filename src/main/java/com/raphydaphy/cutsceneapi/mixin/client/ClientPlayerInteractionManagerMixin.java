package com.raphydaphy.cutsceneapi.mixin.client;


import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import com.raphydaphy.cutsceneapi.fakeworld.FakeWorldInteractionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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
public abstract class ClientPlayerInteractionManagerMixin
{
	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	private GameMode gameMode;

	@Shadow
	protected abstract void syncSelectedSlot();

	@Inject(at = @At("HEAD"), method = "attackEntity", cancellable = true)
	private void attackEntity(PlayerEntity player, Entity entity, CallbackInfo info)
	{
		if (CutsceneManager.isActive(player))
		{
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "breakBlock", cancellable = true)
	private void breakBlock(BlockPos blockPos_1, CallbackInfoReturnable<Boolean> info)
	{
		if (CutsceneManager.isActive(client.player))
		{
			info.setReturnValue(false);
		}
	}

	@Inject(at = @At("HEAD"), method = "attackBlock", cancellable = true)
	private void attackBlock(BlockPos blockPos_1, Direction direction_1, CallbackInfoReturnable<Boolean> info)
	{
		if (CutsceneManager.isActive(client.player))
		{
			info.setReturnValue(false);
		}
	}

	@Inject(at = @At("HEAD"), method = "interactBlock", cancellable = true)
	private void interactBlock(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> info)
	{
		if (world instanceof CutsceneWorld)
		{
			info.setReturnValue(FakeWorldInteractionManager.interactBlock(player, (CutsceneWorld) world, hand, hitResult));
		} else if (CutsceneManager.isActive(client.player))
		{
			info.setReturnValue(ActionResult.PASS);
		}
	}

	@Inject(at = @At("HEAD"), method = "interactItem", cancellable = true)
	private void interactItem(PlayerEntity playerEntity_1, World world_1, Hand hand_1, CallbackInfoReturnable<ActionResult> info)
	{
		if (CutsceneManager.isActive(client.player))
		{
			info.setReturnValue(ActionResult.PASS);
		}
	}
}
