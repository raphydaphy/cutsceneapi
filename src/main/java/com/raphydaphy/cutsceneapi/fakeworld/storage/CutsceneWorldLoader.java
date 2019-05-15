package com.raphydaphy.cutsceneapi.fakeworld.storage;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneChunk;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.*;

public class CutsceneWorldLoader {
    public static void copyCutsceneWorld(Identifier from, String to) {
        InputStream inStream;
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(from);
            inStream = resource.getInputStream();
        } catch (IOException e) {
            CutsceneAPI.log(Level.ERROR, "Failed to copy cutscene world with ID " + from + "! Printing stack trace...");
            e.printStackTrace();
            return;
        }
        if (inStream != null) {
            byte[] buffer;
            try {
                buffer = new byte[inStream.available()];
                inStream.read(buffer);
            } catch (IOException e) {
                CutsceneAPI.log(Level.ERROR, "Failed to copy cutscene world files! Printing stack trace...");
                e.printStackTrace();
                IOUtils.closeQuietly(inStream);
                return;
            }
            File outFile = new File(CutsceneAPIClient.STORAGE.getDirectory(), to);
            if (outFile.exists()) {
                outFile.delete();
            }
            OutputStream outStream = null;
            try {
                outFile.createNewFile();
                outStream = new FileOutputStream(outFile);
                outStream.write(buffer);
            } catch (FileNotFoundException e) {
                CutsceneAPI.log(Level.ERROR, "Couldn't find file to copy cutscene world to... this shouldn't happen! Printing stack trace...");
                e.printStackTrace();
            } catch (IOException e) {
                CutsceneAPI.log(Level.ERROR, "Failed to copy cutscene world! Printing stack trace...");
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(outStream);
            }
            IOUtils.closeQuietly(inStream);
        }
    }

    public static void addChunks(String filename, CutsceneWorld world, int radius) {
        for (int chunkX = -radius; chunkX <= radius; chunkX++) {
            for (int chunkZ = -radius; chunkZ <= radius; chunkZ++) {
                ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
                CompoundTag chunkData;
                try {
                    chunkData = CutsceneAPIClient.STORAGE.getChunkData(filename, chunkPos);
                } catch (IOException e) {
                    CutsceneAPI.log(Level.ERROR, "Failed to deserialize cutscene chunk! Printing stack trace...");
                    e.printStackTrace();
                    continue;
                }
                if (!chunkData.isEmpty()) {
                    Chunk chunk = CutsceneChunkSerializer.deserialize(world, chunkPos, chunkData);
                    CutsceneChunk cutsceneChunk = new CutsceneChunk(world, chunkPos, chunk.getBiomeArray(), getBlockStates(chunk));
                    world.putChunk(chunkPos, cutsceneChunk);
                }
            }
        }
    }

    public static BlockState[] getBlockStates(Chunk chunk) {
        BlockState[] blockStates = new BlockState[16 * chunk.getHeight() * 16];
        int x, y, z, index;
        for (x = 0; x < 16; x++) {
            for (y = 0; y < chunk.getHeight(); y++) {
                for (z = 0; z < 16; z++) {
                    index = z * 16 * chunk.getHeight() + y * 16 + x;
                    blockStates[index] = chunk.getBlockState(new BlockPos(chunk.getPos().getStartX() + x, y, chunk.getPos().getStartZ() + z));
                }
            }
        }
        return blockStates;
    }
}
