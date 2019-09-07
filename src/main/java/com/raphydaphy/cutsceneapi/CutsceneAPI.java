package com.raphydaphy.cutsceneapi;

import com.raphydaphy.crochet.data.PlayerData;
import com.raphydaphy.crochet.network.PacketHandler;
import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.command.CutsceneArgumentSerializer;
import com.raphydaphy.cutsceneapi.command.CutsceneArgumentType;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneCameraEntity;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneRegistry;
import com.raphydaphy.cutsceneapi.cutscene.DefaultCutscene;
import com.raphydaphy.cutsceneapi.network.CutsceneFinishPacket;
import com.raphydaphy.cutsceneapi.network.CutsceneCommandPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CutsceneAPI implements ModInitializer {
    public static final String WATCHING_CUTSCENE_KEY = "WatchingCutscene";
    public static final String CUTSCENE_ID_KEY = "CutsceneID";
    private static final Logger LOGGER = LogManager.getLogger("Cutscene API");
    public static String DOMAIN = "cutsceneapi";
    public static EntityType CUTSCENE_CAMERA_ENTITY;

    public static Cutscene REALWORLD_CUTSCENE = new DefaultCutscene(250);
    public static Cutscene FAKEWORLD_CUTSCENE_1 = new DefaultCutscene(400);
    public static Cutscene FAKEWORLD_CUTSCENE_2 = new DefaultCutscene(200);
    public static Cutscene VOIDWORLD_CUTSCENE = new DefaultCutscene(150);
    public static Cutscene DRAGONSTONE_CUTSCENE = new DefaultCutscene(500);

    public static void log(Level level, String message, Object... data) {
        LOGGER.log(level, "[Cutscene API] " + message, data);
    }

    public static void log(Level level, String message, Exception exception) {
        LOGGER.log(level, "[Cutscene API] " + message, exception);
    }

    @Override
    public void onInitialize() {
        ArgumentTypes.register(DOMAIN + ":cutscene", CutsceneArgumentType.class, new CutsceneArgumentSerializer());

        CutsceneRegistry.register(new Identifier(DOMAIN, "real_world"), REALWORLD_CUTSCENE);
        CutsceneRegistry.register(new Identifier(DOMAIN, "fake_world_1"), FAKEWORLD_CUTSCENE_1);
        CutsceneRegistry.register(new Identifier(DOMAIN, "fake_world_2"), FAKEWORLD_CUTSCENE_2);
        CutsceneRegistry.register(new Identifier(DOMAIN, "void_world"), VOIDWORLD_CUTSCENE);
        CutsceneRegistry.register(new Identifier(DOMAIN, "dragonstone"), DRAGONSTONE_CUTSCENE);

        CUTSCENE_CAMERA_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(DOMAIN, "cutscene_camera"), FabricEntityTypeBuilder.create(EntityCategory.MISC, (t, w) -> new CutsceneCameraEntity(w)).size(new EntityDimensions(1, 1, true)).build());
        ServerSidePacketRegistry.INSTANCE.register(CutsceneFinishPacket.ID, new CutsceneFinishPacket.Handler());

        CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register((CommandManager.literal("cutscene").requires((command) -> command.hasPermissionLevel(2))
        .then(CommandManager.literal("play").then(CommandManager.argument("target", EntityArgumentType.player()).then(CommandManager.argument("cutscene", CutsceneArgumentType.create()).executes(command ->
        {
                Identifier cutscene = CutsceneArgumentType.get(command, "cutscene").getID();
                if (cutscene != null) {
                    ServerPlayerEntity player = EntityArgumentType.getPlayer(command, "target");
                    CutsceneManager.startServer(player, cutscene);
                    return 1;
                }
                return -1;
        }))))).then(CommandManager.literal("stop").then(CommandManager.argument("target", EntityArgumentType.player()).executes((command) -> {
            ServerPlayerEntity player = EntityArgumentType.getPlayer(command, "target");
            PlayerData.get(player, DOMAIN).putBoolean(WATCHING_CUTSCENE_KEY, false);
            PacketHandler.sendToClient(new CutsceneCommandPacket(CutsceneCommandPacket.Command.STOP_PLAYING), player);
            return 1;
        }))).then(CommandManager.literal("record").then(CommandManager.literal("stop")
            .then(CommandManager.argument("target", EntityArgumentType.player()).executes((command) ->
            {
                PacketHandler.sendToClient(new CutsceneCommandPacket(CutsceneCommandPacket.Command.STOP_RECORDING), EntityArgumentType.getPlayer(command, "target"));
                return 1;
            }))).then(CommandManager.literal("camera")
            .then(CommandManager.argument("target", EntityArgumentType.player()).executes((command) ->
            {
                PacketHandler.sendToClient(new CutsceneCommandPacket(CutsceneCommandPacket.Command.RECORD_CAMERA), EntityArgumentType.getPlayer(command, "target"));
                return 1;
            })))).then(CommandManager.literal("world").then(CommandManager.literal("join").then(CommandManager.literal("copy").then(CommandManager.argument("target", EntityArgumentType.player()).executes((command) ->
        {
            PacketHandler.sendToClient(new CutsceneCommandPacket(CutsceneCommandPacket.Command.JOIN_COPY_WORLD), EntityArgumentType.getPlayer(command, "target"));
            return 1;
        }))).then(CommandManager.literal("empty").then(CommandManager.argument("target", EntityArgumentType.player()).executes((command) ->
        {
            PacketHandler.sendToClient(new CutsceneCommandPacket(CutsceneCommandPacket.Command.JOIN_VOID_WORLD), EntityArgumentType.getPlayer(command, "target"));
            return 1;
        }))).then(CommandManager.literal("cached").then(CommandManager.argument("target", EntityArgumentType.player()).executes((command) ->
        {
            PacketHandler.sendToClient(new CutsceneCommandPacket(CutsceneCommandPacket.Command.JOIN_CACHED_WORLD), EntityArgumentType.getPlayer(command, "target"));
            return 1;
        })))).then(CommandManager.literal("leave").then(CommandManager.argument("target", EntityArgumentType.player()).executes((command) ->
        {
            PacketHandler.sendToClient(new CutsceneCommandPacket(CutsceneCommandPacket.Command.LEAVE_WORLD), EntityArgumentType.getPlayer(command, "target"));
            return 1;
        }))).then(CommandManager.literal("serialize").then(CommandManager.argument("target", EntityArgumentType.player()).executes((command) -> {
            PacketHandler.sendToClient(new CutsceneCommandPacket(CutsceneCommandPacket.Command.SERIALIZE_WORLD), EntityArgumentType.getPlayer(command, "target"));
            return 1;
        }))).then(CommandManager.literal("deserialize").then(CommandManager.argument("target", EntityArgumentType.player()).executes((command) -> {
            PacketHandler.sendToClient(new CutsceneCommandPacket(CutsceneCommandPacket.Command.DESERIALIZE_WORLD), EntityArgumentType.getPlayer(command, "target"));
            return 1;
        }))))));

        log(Level.INFO, "Hello there");
    }
}
