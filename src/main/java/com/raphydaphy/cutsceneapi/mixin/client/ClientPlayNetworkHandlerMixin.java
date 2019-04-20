package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.*;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    private ClientWorld world;

    // ********** CUSTOM HANDLER *********** //

    @Inject(at = @At("HEAD"), method = "onBlockUpdate", cancellable = true)
    private void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo info) {
        if (this.world instanceof CutsceneWorld) {
            ClientWorld realWorld = ((CutsceneWorld) this.world).realWorld;
            if (realWorld != null) {
                realWorld.setBlockStateWithoutNeighborUpdates(packet.getPos(), packet.getState());
            }
            info.cancel();
        }
    }

    // ******* CANCEL ******* //

    @Inject(at = @At("HEAD"), method = "onChunkDeltaUpdate", cancellable = true)
    private void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo info) {
        if (world instanceof CutsceneWorld) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onChunkData", cancellable = true)
    private void onChunkData(ChunkDataS2CPacket packet, CallbackInfo info) {
        if (world instanceof CutsceneWorld) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onEntitySpawn", cancellable = true)
    private void onEntitySpawn(EntitySpawnS2CPacket entitySpawnS2CPacket_1, CallbackInfo info) {
        if (this.world instanceof CutsceneWorld) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onUnloadChunk", cancellable = true)
    private void onUnloadChunk(UnloadChunkS2CPacket var1, CallbackInfo info) {
        if (this.world instanceof CutsceneWorld) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onParticle", cancellable = true)
    private void onParticle(ParticleS2CPacket var, CallbackInfo info) {
        if (this.world instanceof CutsceneWorld) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onPlaySound", cancellable = true)
    private void onPlaySound(PlaySoundS2CPacket var1, CallbackInfo info) {
        if (this.world instanceof CutsceneWorld) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onPlaySoundFromEntity", cancellable = true)
    private void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket var1, CallbackInfo info) {
        if (this.world instanceof CutsceneWorld) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "onPlaySoundId", cancellable = true)
    private void onPlaySoundId(PlaySoundIdS2CPacket var1, CallbackInfo info) {
        if (this.world instanceof CutsceneWorld) {
            info.cancel();
        }
    }
}
