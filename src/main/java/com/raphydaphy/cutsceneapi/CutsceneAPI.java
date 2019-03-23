package com.raphydaphy.cutsceneapi;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.raphydaphy.cutsceneapi.command.CutsceneArgumentType;
import com.raphydaphy.cutsceneapi.cutscene.*;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishPacket;
import me.elucent.earlgray.api.TraitEntry;
import me.elucent.earlgray.api.TraitRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CutsceneAPI implements ModInitializer
{
	public static String DOMAIN = "cutsceneapi";
	private static final Logger LOGGER = LogManager.getLogger();

	public static EntityType<CutsceneCameraEntity> CUTSCENE_CAMERA_ENTITY;
	public static TraitEntry<CutsceneTrait> CUTSCENE_TRAIT;

	@Override
	public void onInitialize()
	{
		CutsceneRegistry.register(new Identifier(DOMAIN, "demo"), (player) ->
		{
			float pX = (float) player.x;
			float pY = (float) player.y;
			float pZ = (float) player.z;
			System.out.println(pX + ", " + pY + ", " + pZ);
			return new Cutscene(player, new Path().withPoint(pX + 0, pY + 20, pZ + 0).withPoint(pX + 30, pY + 30, pZ + 10).withPoint(pX + 50, pY + 10, pZ + 10))
					.withDuration(150).withStartSound(SoundEvents.UI_BUTTON_CLICK).withDipTo(20, 255, 255, 255);
		});

		CUTSCENE_CAMERA_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(DOMAIN, "cutscene_camera"), FabricEntityTypeBuilder.create(EntityCategory.MISC, CutsceneCameraEntity::new).size(new EntitySize(1, 1, true)).build());

		CUTSCENE_TRAIT = (TraitEntry<CutsceneTrait>) TraitRegistry.register(new Identifier(DOMAIN, "cutscene_trait"), CutsceneTrait.class);
		TraitRegistry.addInherent(PlayerEntity.class, player -> new CutsceneTrait());

		ServerSidePacketRegistry.INSTANCE.register(CutsceneFinishPacket.ID, new CutsceneFinishPacket.Handler());

		CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register((ServerCommandManager.literal("cutscene").requires((command) -> command.hasPermissionLevel(2))
				.then(ServerCommandManager.argument("target", EntityArgumentType.onePlayer()).then(ServerCommandManager.argument("cutscene", CutsceneArgumentType.create()).executes(command ->
				{
					Identifier cutscene = CutsceneArgumentType.get(command, "cutscene").getID();
					if (cutscene != null)
					{
						CutsceneManager.startServer(EntityArgumentType.getServerPlayerArgument(command, "target"), cutscene);
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
