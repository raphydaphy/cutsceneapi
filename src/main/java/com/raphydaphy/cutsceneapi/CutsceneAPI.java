package com.raphydaphy.cutsceneapi;

import com.raphydaphy.cutsceneapi.command.CutsceneArgumentType;
import com.raphydaphy.cutsceneapi.cutscene.*;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishPacket;
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
						if (!existing.isAir())
						{
							if (!existing.getFluidState().isEmpty())
							{
								return Blocks.AIR.getDefaultState();
							} else
							{
								return Blocks.GRAVEL.getDefaultState();
							}
						}
						return null;
					}).withStartSound(SoundEvents.UI_BUTTON_CLICK).withDipTo(40, 20, 0, 0, 0);
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
				}))))));
	}

	public static Logger getLogger()
	{
		return LOGGER;
	}
}
