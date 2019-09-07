package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.crochet.network.IPacket;
import com.raphydaphy.crochet.network.MessageHandler;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneChunk;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import com.raphydaphy.cutsceneapi.fakeworld.storage.CutsceneChunkSerializer;
import com.raphydaphy.cutsceneapi.fakeworld.storage.CutsceneWorldLoader;
import com.raphydaphy.cutsceneapi.path.PathRecorder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class CutsceneCommandPacket implements IPacket {
    public static final Identifier ID = new Identifier(CutsceneAPI.DOMAIN, "world_test");

    private Command test;

    private CutsceneCommandPacket() {

    }

    public CutsceneCommandPacket(Command test) {
        this.test = test;
    }

    @Override
    public void read(PacketByteBuf buf) {
        test = Command.values()[buf.readInt()];
    }

    @Override
    public void write(PacketByteBuf buf) {
        int id = 0;
        for (Command command : Command.values()) {
            if (command == this.test) {
                buf.writeInt(id);
                return;
            }
            id++;
        }
        buf.writeInt(0);
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    public enum Command {
        JOIN_VOID_WORLD, JOIN_COPY_WORLD, JOIN_CACHED_WORLD, LEAVE_WORLD, SERIALIZE_WORLD, DESERIALIZE_WORLD, RECORD_CAMERA, STOP_RECORDING, STOP_PLAYING
    }

    @Environment(EnvType.CLIENT)
    public static class Handler extends MessageHandler<CutsceneCommandPacket> {
        @Override
        protected CutsceneCommandPacket create() {
            return new CutsceneCommandPacket();
        }

        @Override
        public void handle(PacketContext ctx, CutsceneCommandPacket message) {
            MinecraftClient client = MinecraftClient.getInstance();
            Command command = message.test;
            if (command == Command.JOIN_COPY_WORLD || command == Command.JOIN_VOID_WORLD || command == Command.JOIN_CACHED_WORLD) {
                if (command == Command.JOIN_CACHED_WORLD) {
                    CutsceneWorld cutsceneWorld = new CutsceneWorld(client, client.world, null, false);
                    int radius = 15;
                    for (int chunkX = -radius; chunkX <= radius; chunkX++) {
                        for (int chunkZ = -radius; chunkZ <= radius; chunkZ++) {
                            ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
                            CompoundTag chunkData;
                            try {
                                chunkData = CutsceneAPIClient.STORAGE.getChunkData("serialized.cworld", chunkPos);
                            } catch (IOException e) {
                                CutsceneAPI.log(Level.ERROR, "Failed to deserialize cutscene chunk! Printing stack trace...");
                                e.printStackTrace();
                                continue;
                            }
                            if (!chunkData.isEmpty()) {
                                Chunk chunk = CutsceneChunkSerializer.deserialize(cutsceneWorld, chunkPos, chunkData);
                                CutsceneChunk cutsceneChunk = new CutsceneChunk(cutsceneWorld, chunkPos, chunk.getBiomeArray(), CutsceneWorldLoader.getBlockStates(chunk));
                                cutsceneWorld.putChunk(chunkPos, cutsceneChunk);
                            }
                        }
                    }
                    CutsceneManager.startFakeWorld(cutsceneWorld, false);
                } else {
                    boolean copy = command == Command.JOIN_COPY_WORLD;
                    CutsceneManager.startFakeWorld(new CutsceneWorld(client, client.world, null, copy), !copy);
                }
            } else if (command == Command.LEAVE_WORLD) {
                CutsceneManager.stopFakeWorld();
            } else if (command == Command.RECORD_CAMERA) {
                PathRecorder.start();
                client.player.addChatMessage(new TranslatableText("command.cutsceneapi.startrecording"), true);
            } else if (command == Command.STOP_RECORDING) {
                PathRecorder.stop();
                client.player.addChatMessage(new TranslatableText("command.cutsceneapi.stoprecording"), true);
            } else if (command == Command.SERIALIZE_WORLD) {
                int radius = 15;
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        Chunk chunk = client.world.getChunk(new BlockPos(x * 16, 0, z * 16));
                        CutsceneChunkSerializer.serializeAndSave(client.world, chunk);
                    }
                }
                client.player.addChatMessage(new TranslatableText("command.cutsceneapi.serializedchunk"), false);
            } else if (command == Command.DESERIALIZE_WORLD) {
                CompoundTag tag;
                ChunkPos pos = new ChunkPos(0, 0);
                try {
                    tag = CutsceneAPIClient.STORAGE.getChunkData("serialized.cworld", pos);
                } catch (IOException e) {
                    CutsceneAPI.log(Level.ERROR, "Failed to deserialize cutscene chunk! Printing stack trace...");
                    e.printStackTrace();
                    return;
                }
                if (!tag.isEmpty()) {
                    Chunk chunk = CutsceneChunkSerializer.deserialize(client.world, pos, tag);
                    CutsceneAPI.log(Level.DEBUG, "Got chunk! Block at {0, 0, 0}: " + chunk.getBlockState(new BlockPos(pos.x * 16, 0, pos.z * 16)).getBlock());
                    client.player.addChatMessage(new TranslatableText("command.cutsceneapi.deserialized"), false);
                } else {
                    client.player.addChatMessage(new TranslatableText("command.cutsceneapi.didntserialize"), false);
                }
            } else if (command == Command.STOP_PLAYING) {
                CutsceneManager.stopCutscene();
            }
        }
    }
}
