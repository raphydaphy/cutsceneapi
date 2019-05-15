package com.raphydaphy.cutsceneapi.fakeworld.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.RegionFile;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CutsceneWorldStorage implements AutoCloseable {
    private File directory;
    private Map<String, RegionFile> regionFiles;

    public CutsceneWorldStorage() {
        this.directory = new File("cutscenes/worlds");
        this.regionFiles = new HashMap<>();
    }

    public File getDirectory() {
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
        return directory;
    }

    private RegionFile getRegionFile(String filename) throws IOException {
        if (this.regionFiles.containsKey(filename)) {
            return this.regionFiles.get(filename);
        } else {
            File file = new File(getDirectory(), filename);
            this.regionFiles.put(filename, new RegionFile(file));
            return regionFiles.get(filename);
        }
    }

    public CompoundTag getChunkData(String filename, ChunkPos pos) throws IOException {
        RegionFile regionFile = getRegionFile(filename);
        if (regionFile.hasChunk(pos)) {
            DataInputStream inputStream = regionFile.getChunkDataInputStream(pos);
            Throwable exception = null;

            CompoundTag tag;
            try {
                if (inputStream != null) {
                    return NbtIo.read(inputStream);
                }

                tag = new CompoundTag();
            } catch (Throwable e) {
                exception = e;
                throw e;
            } finally {
                handleException(inputStream, exception);
            }

            return tag;
        } else {
            return new CompoundTag();
        }
    }

    public void setChunkData(String filename, ChunkPos chunkPos, CompoundTag chunkData) throws IOException {
        RegionFile regionFile = this.getRegionFile(filename);
        DataOutputStream outputStream = regionFile.getChunkDataOutputStream(chunkPos);
        Throwable exception = null;
        try {
            NbtIo.write(chunkData, outputStream);
        } catch (Throwable var14) {
            exception = var14;
            throw var14;
        } finally {
            handleException(outputStream, exception);
        }
    }

    private void handleException(Closeable closable, Throwable exception) throws IOException {
        if (closable != null) {
            if (exception != null) {
                try {
                    closable.close();
                } catch (Throwable var15) {
                    exception.addSuppressed(var15);
                }
            } else {
                closable.close();
            }
        }
    }

    @Override
    public void close() throws IOException {
        for (Map.Entry<String, RegionFile> entry : regionFiles.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().close();
            }
        }
    }
}
