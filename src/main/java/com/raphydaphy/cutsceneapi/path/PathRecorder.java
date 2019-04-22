package com.raphydaphy.cutsceneapi.path;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Environment(EnvType.CLIENT)
public class PathRecorder {
    private static RecordedPath.Builder buildingPath;
    private static boolean recording = false;

    public static void start() {
        buildingPath = RecordedPath.builder();
        recording = true;
    }

    public static void stop() {
        if (recording) {
            recording = false;
            if (buildingPath != null) {
                CompoundTag tag = buildingPath.toTag();
                File dir = new File("cutscene_paths");
                if (!dir.exists()) {
                    dir.mkdir();
                }

                OutputStream stream = null;
                try {
                    stream = new FileOutputStream(new File(dir, "recorded.cpath"));
                    NbtIo.writeCompressed(tag, stream);
                } catch (IOException e) {
                    CutsceneAPI.log(Level.ERROR, "Failed to serialize recorded cutscene path! Printing stack trace...");
                    e.printStackTrace();
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            CutsceneAPI.log(Level.ERROR, "Failed to close output stream! Printing stack trace...");
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                CutsceneAPI.log(Level.ERROR, "Tried to stop a cutscene path recording without a valid path builder!");
            }
        } else {
            CutsceneAPI.log(Level.ERROR, "Tried to stop a non-existent cutscene path recording!");
        }
    }

    public static boolean isRecording() {
        return recording;
    }

    public static void tick() {
        if (recording) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (buildingPath == null) {
                CutsceneAPI.log(Level.ERROR, "Tried to a cutscene path record without a valid path builder!");
                recording = false;
            } else {
                buildingPath.with(new Vector3f((float) client.player.x, (float) client.player.y, (float) client.player.z), client.player.pitch, client.player.yaw);
            }
        }
    }
}
