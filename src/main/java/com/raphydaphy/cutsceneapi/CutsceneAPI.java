package com.raphydaphy.cutsceneapi;

import com.raphydaphy.crochet.network.PacketHandler;
import com.raphydaphy.cutsceneapi.api.CutsceneEntry;
import com.raphydaphy.cutsceneapi.command.CutsceneArgumentType;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneCameraEntity;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneRegistry;
import com.raphydaphy.cutsceneapi.cutscene.DefaultCutscene;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishPacket;
import com.raphydaphy.cutsceneapi.network.WorldTestPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
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

	public static CutsceneEntry REALWORLD_CUTSCENE;
	public static CutsceneEntry FAKEWORLD_CUTSCENE_1;
	public static CutsceneEntry FAKEWORLD_CUTSCENE_2;
	public static CutsceneEntry VOIDWORLD_CUTSCENE;

	@Override
	public void onInitialize()
	{
		REALWORLD_CUTSCENE = CutsceneRegistry.register(new Identifier(DOMAIN, "real_world"), new DefaultCutscene(250));
		FAKEWORLD_CUTSCENE_1 = CutsceneRegistry.register(new Identifier(DOMAIN, "fake_world_1"), new DefaultCutscene(400));
		FAKEWORLD_CUTSCENE_2 = CutsceneRegistry.register(new Identifier(DOMAIN, "fake_world_2"), new DefaultCutscene(200));
		VOIDWORLD_CUTSCENE = CutsceneRegistry.register(new Identifier(DOMAIN, "void_world"), new DefaultCutscene(150));

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
		}))).then(ServerCommandManager.literal("empty").then(ServerCommandManager.argument("target", EntityArgumentType.onePlayer()).executes((command) ->
		{
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
