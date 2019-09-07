package com.raphydaphy.cutsceneapi.fakeworld;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FakeWorldInteractionManager {
    public static ActionResult interactBlock(ClientPlayerEntity player, CutsceneWorld world, Hand hand, BlockHitResult hitResult) {
        MinecraftClient client = MinecraftClient.getInstance();
        BlockPos blockPos_1 = hitResult.getBlockPos();
        Vec3d vec3d_1 = hitResult.getPos();
        if (!client.world.getWorldBorder().contains(blockPos_1)) {
            return ActionResult.FAIL;
        } else {
            ItemStack itemStack_1 = player.getStackInHand(hand);
            if (player.isSpectator()) {
                handlePlayerInteractBlock(hand, hitResult);
                return ActionResult.SUCCESS;
            } else {
                boolean boolean_1 = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
                boolean boolean_2 = player.isSneaking() && boolean_1;
                if (!boolean_2 && world.getBlockState(blockPos_1).activate(world, player, hand, hitResult)) {
                    handlePlayerInteractBlock(hand, hitResult);
                    return ActionResult.SUCCESS;
                } else {
                    handlePlayerInteractBlock(hand, hitResult);
                    if (!itemStack_1.isEmpty() && !player.getItemCooldownManager().isCoolingDown(itemStack_1.getItem())) {
                        ItemUsageContext itemUsageContext_1 = new ItemUsageContext(player, hand, hitResult);
                        ActionResult result;
                        if (player.isCreative()) {
                            int int_1 = itemStack_1.getCount();
                            result = itemStack_1.useOnBlock(itemUsageContext_1);
                            itemStack_1.setCount(int_1);
                        } else {
                            result = itemStack_1.useOnBlock(itemUsageContext_1);
                        }

                        return result;
                    } else {
                        return ActionResult.PASS;
                    }
                }
            }
        }
    }

    private static void handlePlayerInteractBlock(Hand hand, BlockHitResult hitResult) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        ItemStack stack = client.player.getStackInHand(hand);
        BlockPos blockPos_1 = hitResult.getBlockPos();
        Direction direction_1 = hitResult.getSide();
        if (blockPos_1.getY() >= world.getHeight() - 1 && (direction_1 == Direction.UP || blockPos_1.getY() >= world.getHeight())) {
            client.player.addChatMessage(new TranslatableText("build.tooHigh", world.getHeight()).formatted(Formatting.RED), true);
        } else if (client.player.squaredDistanceTo((double) blockPos_1.getX() + 0.5D, (double) blockPos_1.getY() + 0.5D, (double) blockPos_1.getZ() + 0.5D) < 64.0D && world.getWorldBorder().contains(blockPos_1)) {
            interactBlock(client.player, world, stack, hand, hitResult);
        }
    }

    private static ActionResult interactBlock(PlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult) {
        BlockPos blockPos_1 = hitResult.getBlockPos();
        BlockState blockState_1 = world.getBlockState(blockPos_1);
        if (player.isSpectator()) {
            NameableContainerProvider nameableContainerProvider_1 = blockState_1.createContainerProvider(world, blockPos_1);
            if (nameableContainerProvider_1 != null) {
                player.openContainer(nameableContainerProvider_1);
                return ActionResult.SUCCESS;
            } else {
                return ActionResult.PASS;
            }
        } else {
            boolean boolean_1 = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
            boolean boolean_2 = player.isSneaking() && boolean_1;
            if (!boolean_2 && blockState_1.activate(world, player, hand, hitResult)) {
                return ActionResult.SUCCESS;
            } else if (!stack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
                ItemUsageContext itemUsageContext_1 = new ItemUsageContext(player, hand, hitResult);
                if (player.isCreative()) {
                    int int_1 = stack.getCount();
                    ActionResult actionResult_1 = stack.useOnBlock(itemUsageContext_1);
                    stack.setCount(int_1);
                    return actionResult_1;
                } else {
                    return stack.useOnBlock(itemUsageContext_1);
                }
            } else {
                return ActionResult.PASS;
            }
        }
    }

    public static ActionResult interactItem(PlayerEntity player, World world_1, Hand hand_1) {
        if (player.isSpectator()) {
            return ActionResult.PASS;
        } else {
            ItemStack itemStack_1 = player.getStackInHand(hand_1);
            if (player.getItemCooldownManager().isCoolingDown(itemStack_1.getItem())) {
                return ActionResult.PASS;
            } else {
                int int_1 = itemStack_1.getCount();
                TypedActionResult<ItemStack> typedActionResult_1 = itemStack_1.use(world_1, player, hand_1);
                ItemStack itemStack_2 = typedActionResult_1.getValue();
                if (itemStack_2 != itemStack_1 || itemStack_2.getCount() != int_1) {
                    player.setStackInHand(hand_1, itemStack_2);
                }

                return typedActionResult_1.getResult();
            }
        }
    }
}
