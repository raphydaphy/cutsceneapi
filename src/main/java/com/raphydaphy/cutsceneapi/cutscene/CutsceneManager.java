package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.crochet.data.PlayerData;
import com.raphydaphy.crochet.network.PacketHandler;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import com.raphydaphy.cutsceneapi.mixin.client.ClientPlayNetworkHandlerHooks;
import com.raphydaphy.cutsceneapi.mixin.client.MinecraftClientHooks;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishPacket;
import com.raphydaphy.cutsceneapi.network.CutsceneStartPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

public class CutsceneManager
{
	private static Cutscene currentCutscene;

	public static boolean hideHud(PlayerEntity player)
	{
		return isActive(player) && currentCutscene != null && currentCutscene.hideHud();
	}

	public static boolean isActive(PlayerEntity player)
	{
		return player != null && PlayerData.get(player).getBoolean(CutsceneAPI.WATCHING_CUTSCENE_KEY);
	}

	@Environment(EnvType.CLIENT)
	public static BlockState getFakeWorldState(BlockPos pos, BlockState existing)
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (existing != null && hideHud(client.player) && showFakeWorld())
		{
			return currentCutscene.getBlockRemapper().apply(pos, existing);
		}
		return null;
	}

	@Environment(EnvType.CLIENT)
	public static FluidState getFakeWorldFluid(BlockPos pos)
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (hideHud(client.player) && showFakeWorld())
		{
			return currentCutscene.getFluidRemapper().apply(pos);
		}
		return null;
	}

	@Environment(EnvType.CLIENT)
	public static int getFakeWorldLight(LightType type, BlockPos pos)
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (hideHud(client.player) && showFakeWorld())
		{
			return 15;
		}
		return -1;
	}

	@Environment(EnvType.CLIENT)
	public static void updateLook()
	{
		if (currentCutscene != null)
		{
			currentCutscene.updateLook();
		}
	}

	public static boolean showFakeWorld()
	{
		if (currentCutscene != null)
		{
			return currentCutscene.usesFakeWorld();
		}
		return false;
	}

	@Environment(EnvType.CLIENT)
	public static void renderHud()
	{
		if (currentCutscene != null)
		{
			currentCutscene.renderTransitions();
		}
	}

	@Environment(EnvType.CLIENT)
	public static void startClient(Identifier cutscene)
	{
		MinecraftClient client = MinecraftClient.getInstance();
		client.getSoundManager().stopAll();
		currentCutscene = CutsceneRegistry.get(cutscene, client.player);
		currentCutscene.start(client.player);
	}

	private static ClientWorld realWorld;

	@Environment(EnvType.CLIENT)
	public static void startFakeWorld()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		realWorld = client.world;
		CutsceneWorld cutsceneWorld = new CutsceneWorld(client, client.world);
		client.player.setWorld(cutsceneWorld);
		client.world = cutsceneWorld;
		((MinecraftClientHooks) client).setCutsceneWorld(cutsceneWorld);
		ClientPlayNetworkHandler handler = client.getNetworkHandler();
		if (handler != null)
		{
			((ClientPlayNetworkHandlerHooks) handler).setCutsceneWorld(cutsceneWorld);
		}

		cutsceneWorld.setBlockState(client.player.getBlockPos().down(), Blocks.DIORITE.getDefaultState());
		cutsceneWorld.addPlayer(client.player);

		client.inGameHud.setTitles("§5Welcome!§r", "", 20, 50, 20);
	}

	@Environment(EnvType.CLIENT)
	public static ClientWorld getRealWorld()
	{
		return realWorld;
	}

	@Environment(EnvType.CLIENT)
	public static void stopFakeWorld()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world instanceof CutsceneWorld && realWorld != null)
		{
			client.player.setWorld(realWorld);
			client.world = realWorld;
			((MinecraftClientHooks) client).setCutsceneWorld(realWorld);
			ClientPlayNetworkHandler handler = client.getNetworkHandler();
			if (handler != null)
			{
				((ClientPlayNetworkHandlerHooks) handler).setCutsceneWorld(realWorld);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static void finishClient()
	{
		boolean reload = false;
		if (currentCutscene != null)
		{
			reload = currentCutscene.usesFakeWorld();
			currentCutscene = null;
		}
		PacketHandler.sendToServer(new CutsceneFinishPacket());

		if (reload)
		{
			MinecraftClient.getInstance().worldRenderer.reload();
		}
	}

	@Environment(EnvType.CLIENT)
	public static void updateClient()
	{
		MinecraftClient client = MinecraftClient.getInstance();
		if (isActive(client.player))
		{
			if (currentCutscene == null)
				currentCutscene = CutsceneRegistry.get(Identifier.create(PlayerData.get(client.player).getString(CutsceneAPI.CUTSCENE_ID_KEY)), client.player);
			if (currentCutscene != null) currentCutscene.updateClient();
		}
	}

	public static void startServer(ServerPlayerEntity player, Identifier id)
	{
		player.stopRiding();
		PlayerData.get(player).putBoolean(CutsceneAPI.WATCHING_CUTSCENE_KEY, true);
		PlayerData.get(player).putString(CutsceneAPI.CUTSCENE_ID_KEY, id.toString());
		PlayerData.markDirty(player);
		PacketHandler.sendToClient(new CutsceneStartPacket(id), player);
	}

	public static void finishServer(PlayerEntity player)
	{
		PlayerData.get(player).putBoolean(CutsceneAPI.WATCHING_CUTSCENE_KEY, false);
		PlayerData.markDirty(player);
	}
}
