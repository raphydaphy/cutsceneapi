package com.raphydaphy.cutsceneapi.mixin.client;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.util.ThreadTaskQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
	@Shadow
	private ClientWorld world;

	@Shadow
	private MinecraftClient client;

	// ********** CUSTOM HANDLER *********** //

	@Inject(at = @At("HEAD"), method = "onBlockUpdate", cancellable = true)
	private void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo info)
	{
		if (this.world instanceof CutsceneWorld)
		{
			ClientWorld realWorld = CutsceneManager.getRealWorld();
			if (realWorld != null)
			{
				realWorld.method_2937(packet.getPos(), packet.getState());
			}
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "onChunkDeltaUpdate", cancellable = true)
	private void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket packet, CallbackInfo info)
	{
		if (world instanceof CutsceneWorld)
		{
			ClientWorld realWorld = CutsceneManager.getRealWorld();
			if (realWorld != null)
			{
				NetworkThreadUtils.forceMainThread(packet, (ClientPlayNetworkHandler) (Object) this, this.client);
				ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[] deltaRecords = packet.getRecords();

				for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord deltaRecord : deltaRecords)
				{
					realWorld.method_2937(deltaRecord.getBlockPos(), deltaRecord.getState());
				}
			}
			info.cancel();
		}
	}

	// ********* REDIRECT *********** //

	@Inject(at = @At("HEAD"), method = "onEntitySpawn")
	private void onEntitySpawnHead(EntitySpawnS2CPacket entitySpawnS2CPacket_1, CallbackInfo info)
	{
		if (this.world instanceof CutsceneWorld)
		{
			ClientWorld realWorld = CutsceneManager.getRealWorld();
			if (realWorld != null)
			{
				this.world = realWorld;
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "onEntitySpawn")
	private void onEntitySpawnReturn(EntitySpawnS2CPacket entitySpawnS2CPacket_1, CallbackInfo info)
	{
		if (client.world instanceof CutsceneWorld && !(this.world instanceof CutsceneWorld))
		{
			this.world = client.world;
		}
	}

	@Inject(at = @At("HEAD"), method = "onChunkData")
	private void onChunkDataHead(ChunkDataS2CPacket var1, CallbackInfo info)
	{
		if (this.world instanceof CutsceneWorld)
		{
			ClientWorld realWorld = CutsceneManager.getRealWorld();
			if (realWorld != null)
			{
				this.world = realWorld;
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "onChunkData")
	private void onChunkDataReturn(ChunkDataS2CPacket var1, CallbackInfo info)
	{
		if (client.world instanceof CutsceneWorld && !(this.world instanceof CutsceneWorld))
		{
			this.world = client.world;
		}
	}

	@Inject(at = @At("HEAD"), method = "onUnloadChunk")
	private void onUnloadChunkHead(UnloadChunkS2CPacket var1, CallbackInfo info)
	{
		if (this.world instanceof CutsceneWorld)
		{
			ClientWorld realWorld = CutsceneManager.getRealWorld();
			if (realWorld != null)
			{
				this.world = realWorld;
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "onUnloadChunk")
	private void onUnloadChunkReturn(UnloadChunkS2CPacket var1, CallbackInfo info)
	{
		if (client.world instanceof CutsceneWorld && !(this.world instanceof CutsceneWorld))
		{
			this.world = client.world;
		}
	}

	// ******* CANCEL ******* //

	@Inject(at = @At("HEAD"), method = "onParticle", cancellable = true)
	private void onParticle(ParticleS2CPacket var, CallbackInfo info)
	{
		if (this.world instanceof CutsceneWorld)
		{
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "onPlaySound", cancellable = true)
	private void onPlaySound(PlaySoundS2CPacket var1, CallbackInfo info)
	{
		if (this.world instanceof CutsceneWorld)
		{
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "onPlaySoundFromEntity", cancellable = true)
	private void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket var1, CallbackInfo info)
	{
		if (this.world instanceof CutsceneWorld)
		{
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "onPlaySoundId", cancellable = true)
	private void onPlaySoundId(PlaySoundIdS2CPacket var1, CallbackInfo info)
	{
		if (this.world instanceof CutsceneWorld)
		{
			info.cancel();
		}
	}
}
