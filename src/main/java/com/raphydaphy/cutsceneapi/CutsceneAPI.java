package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.command.CutsceneArgumentType;
import com.raphydaphy.cutsceneapi.cutscene.*;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishPacket;
import com.raphydaphy.cutsceneapi.util.ModDimensionType;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.TheEndDimension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CutsceneAPI implements ModInitializer
{
	public static String DOMAIN = "cutsceneapi";
	private static final Logger LOGGER = LogManager.getLogger();

	public static final String WATCHING_CUTSCENE_KEY = "WatchingCutscene";
	public static final String CUTSCENE_ID_KEY = "CutsceneID";

	public static EntityType<CutsceneCameraEntity> CUTSCENE_CAMERA_ENTITY;

	public static DimensionType CUTSCENE_DIMENSION;

	@Override
	public void onInitialize()
	{
		CutsceneRegistry.register(new Identifier(DOMAIN, "demo"), (player) ->
		{
			float pX = (float) player.x;
			float pY = (float) player.y;
			float pZ = (float) player.z;
			return new Cutscene(player, new Path().withPoint(pX + 0, pY + 20, pZ + 0).withPoint(pX + 30, pY + 30, pZ + 10).withPoint(pX + 50, pY + 10, pZ + 10))
					.withDuration(150).setFakeWorld().withStartSound(SoundEvents.UI_BUTTON_CLICK).withDipTo(20, 0, 0, 0);
		});

		CUTSCENE_DIMENSION = Registry.register(Registry.DIMENSION, 64, "cutscene_dimension", new ModDimensionType(64, "_cutscene_dim", "CUTSCENE_DIMENSION", CutsceneDimension::new, false));

		CUTSCENE_CAMERA_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(DOMAIN, "cutscene_camera"), FabricEntityTypeBuilder.create(EntityCategory.MISC, CutsceneCameraEntity::new).size(new EntitySize(1, 1, true)).build());
		ServerSidePacketRegistry.INSTANCE.register(CutsceneFinishPacket.ID, new CutsceneFinishPacket.Handler());

		CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register((ServerCommandManager.literal("cutscene").requires((command) -> command.hasPermissionLevel(2))
				.then(ServerCommandManager.argument("target", EntityArgumentType.onePlayer()).then(ServerCommandManager.argument("cutscene", CutsceneArgumentType.create()).executes(command ->
				{
					Identifier cutscene = CutsceneArgumentType.get(command, "cutscene").getID();
					if (cutscene != null)
					{
						ServerPlayerEntity player= EntityArgumentType.getServerPlayerArgument(command, "target");
						player.changeDimension(CutsceneAPI.CUTSCENE_DIMENSION);
						//CutsceneManager.startServer(, cutscene);
						return 1;
					}
					return -1;
				}))))));
	}

	public static Logger getLogger()
	{
		return LOGGER;
	}
}
