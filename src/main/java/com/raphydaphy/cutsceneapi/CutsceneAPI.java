package com.raphydaphy.cutsceneapi;

import com.raphydaphy.crochet.network.PacketHandler;
import com.raphydaphy.cutsceneapi.command.CutsceneArgumentType;
import com.raphydaphy.cutsceneapi.cutscene.*;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishPacket;
import com.raphydaphy.cutsceneapi.network.WorldTestPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CutsceneAPI implements ModInitializer
{
	public static String DOMAIN = "cutsceneapi";
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String WATCHING_CUTSCENE_KEY = "WatchingCutscene";
	public static final String CUTSCENE_ID_KEY = "CutsceneID";

	public static EntityType<CutsceneCameraEntity> CUTSCENE_CAMERA_ENTITY;

	@Override
	public void onInitialize()
	{
		CutsceneRegistry.register(new Identifier(DOMAIN, "demo"), (player) ->
		{
			float pX = (float) player.x;
			float pY = (float) player.y;
			float pZ = (float) player.z;
			return new Cutscene(player, new Path().withPoint(pX + 0, pY + 20, pZ + 0).withPoint(pX + 30, pY + 30, pZ + 10).withPoint(pX + 50, pY + 10, pZ + 10))
					.withDuration(250).withFakeWorld().withBlockRemapper((pos, existing) ->
					{
						if (pos.getY() < 30)
						{
							return Blocks.GRAVEL.getDefaultState();
						}
						return Blocks.AIR.getDefaultState();
					}).withStartSound(SoundEvents.UI_BUTTON_CLICK).withDipTo(40, 20, 0, 0, 0);
		});

		CutsceneRegistry.register(new Identifier(DOMAIN, "end"), (player) ->
		{
			return new Cutscene(player, new Path().withPoint(140, 40, 0).withPoint(44, 75, 16).withPoint(-80, 56, 12))
					.withDuration(250).withTransition(new Transition.DipTo(0, 40, 0, 0, 0, 0).setIntro())
					.withTransition(new Transition.FadeFrom(210, 40, 0, 0, 0)).withCutscene(
							new Cutscene(player, new Path().withPoint(54, 106, 64).withPoint(4, 65, 4).withPoint(-50, 100, -33))
									.withDuration(100).withTransition(new Transition.FadeTo(0, 40, 0, 0, 0)).withTransition(new Transition.DipTo(60, 40, 0, 0, 0, 0).setOutro()));
		});

		CUTSCENE_CAMERA_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(DOMAIN, "cutscene_camera"), FabricEntityTypeBuilder.create(EntityCategory.MISC, CutsceneCameraEntity::new).size(new EntitySize(1, 1, true)).build());
		ServerSidePacketRegistry.INSTANCE.register(CutsceneFinishPacket.ID, new CutsceneFinishPacket.Handler());

		CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register((ServerCommandManager.literal("cutscene").requires((command) -> command.hasPermissionLevel(2))
		.then(ServerCommandManager.argument("target", EntityArgumentType.onePlayer()).then(ServerCommandManager.argument("cutscene", CutsceneArgumentType.create()).executes(command ->
		{
			Identifier cutscene = CutsceneArgumentType.get(command, "cutscene").getID();
			if (cutscene != null)
			{
				ServerPlayerEntity player = EntityArgumentType.getServerPlayerArgument(command, "target");
				CutsceneManager.startServer(player, cutscene);
				return 1;
			}
			return -1;
		})))).then(ServerCommandManager.literal("world").then(ServerCommandManager.literal("join").then(ServerCommandManager.literal("copy").then(ServerCommandManager.argument("target", EntityArgumentType.onePlayer()).executes((command) ->
		{
			PacketHandler.sendToClient(new WorldTestPacket(true, true), EntityArgumentType.getServerPlayerArgument(command, "target"));
			return 1;
		}))).then(ServerCommandManager.literal("empty").then(ServerCommandManager.argument("target", EntityArgumentType.onePlayer()).executes((command) -> {
			PacketHandler.sendToClient(new WorldTestPacket(true, false), EntityArgumentType.getServerPlayerArgument(command, "target"));
			return 1;
		})))).then(ServerCommandManager.literal("leave").then(ServerCommandManager.argument("target", EntityArgumentType.onePlayer()).executes((command) ->
		{
			PacketHandler.sendToClient(new WorldTestPacket(false, false), EntityArgumentType.getServerPlayerArgument(command, "target"));
			return 1;
		}))))));
	}

	public static Logger getLogger()
	{
		return LOGGER;
	}
}
