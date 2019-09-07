package com.raphydaphy.cutsceneapi.path;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.utils.CutsceneUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RecordedPath implements Path {
    // Length in ticks
    private final int length;

    // One per tick
    private final Vector3f[] positions;
    private final float[] pitch;
    private final float[] yaw;

    public RecordedPath(Vector3f[] positions, float[] pitch, float[] yaw) {
        int totalPositions = positions.length;
        if (totalPositions != pitch.length || totalPositions != yaw.length) {
            CutsceneAPI.log(Level.ERROR, "Tried to create a RecordedPath with different length position/pitch/yaw arrays!");
        }
        this.length = totalPositions;
        this.positions = positions;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public static RecordedPath.Builder builder() {
        return new RecordedPath.Builder();
    }

    public static RecordedPath fromFile(Identifier id) {
        InputStream stream = null;
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id);
            stream = resource.getInputStream();
        } catch (IOException e) {
            CutsceneAPI.log(Level.ERROR, "Failed to load cutscene path with ID " + id + "! Printing stack trace...");
            e.printStackTrace();
            return null;
        }
        if (stream != null) {
            return fromInputStream(stream);
        }
        return null;
    }

    public static RecordedPath fromInputStream(InputStream stream) {
        CompoundTag tag = null;
        try {
            tag = NbtIo.readCompressed(stream);
        } catch (IOException e) {
            CutsceneAPI.log(Level.ERROR, "Failed to read cutscene path! Printing stack trace...");
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    CutsceneAPI.log(Level.ERROR, "Failed to close input stream! Printing stack trace...");
                    e.printStackTrace();
                }
            }
        }
        if (tag != null) {
            return fromTag(tag);
        }
        return null;
    }

    public static RecordedPath fromFile(String filename) {
        File dir = new File("cutscene_paths");
        if (dir.exists()) {
            InputStream stream = null;
            try {
                stream = new FileInputStream(new File(dir, filename));
            } catch (IOException e) {
                CutsceneAPI.log(Level.ERROR, "Failed to create stream! Printing stack trace...");
                e.printStackTrace();
            }
            if (stream != null) {
                return fromInputStream(stream);
            }
        }
        return null;
    }

    public static RecordedPath fromTag(CompoundTag tag) {
        int length = tag.getInt("Length");
        RecordedPath.Builder builder = builder();
        for (int i = 0; i < length; i++) {
            CompoundTag entry = (CompoundTag) tag.getTag("Entry" + i);
            if (entry == null) continue;
            builder.with(new Vector3f(entry.getFloat("PosX"), entry.getFloat("PosY"), entry.getFloat("PosZ")), entry.getFloat("Pitch"), entry.getFloat("Yaw"));
        }
        return builder.build();
    }

    @Override
    public Vector3f getPoint(float position) {
        float index = position * length;
        int cur = (int) index;
        float time = index - cur;

        Vector3f curPos = positions[cur];
        Vector3f prevPos = positions[cur <= 0 ? cur : cur - 1];

        return new Vector3f(CutsceneUtils.lerp(prevPos.getX(), curPos.getX(), time), CutsceneUtils.lerp(prevPos.getY(), curPos.getY(), time), CutsceneUtils.lerp(prevPos.getZ(), curPos.getZ(), time));
    }

    @Override
    public Pair<Float, Float> getRotation(float position) {
        float index = position * length;
        int cur = (int) index;
        int prev = cur <= 0 ? cur : cur - 1;
        float time = index - cur;

        return new Pair<>(CutsceneUtils.lerp(pitch[prev], pitch[cur], time), CutsceneUtils.lerp(yaw[prev], yaw[cur], time));
    }

    public static class Builder {
        // One position/pitch/yaw per tick
        private final List<Vector3f> positions;
        private final List<Float> pitch;
        private final List<Float> yaw;

        private Builder() {
            this.positions = new ArrayList<>();
            this.pitch = new ArrayList<>();
            this.yaw = new ArrayList<>();
        }

        public Builder with(Vector3f point, float pitch, float yaw) {
            this.positions.add(point);
            this.pitch.add(pitch);
            this.yaw.add(yaw);
            return this;
        }

        public CompoundTag toTag() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("Length", positions.size());
            for (int i = 0; i < positions.size(); i++) {
                CompoundTag entry = new CompoundTag();
                Vector3f pos = positions.get(i);
                entry.putFloat("PosX", pos.getX());
                entry.putFloat("PosY", pos.getY());
                entry.putFloat("PosZ", pos.getZ());
                entry.putFloat("Pitch", pitch.get(i));
                entry.putFloat("Yaw", yaw.get(i));
                tag.put("Entry" + i, entry);
            }
            return tag;
        }

        public RecordedPath build() {
            Vector3f[] positions = new Vector3f[this.positions.size()];
            float[] pitch = new float[this.pitch.size()];
            float[] yaw = new float[this.yaw.size()];
            for (int i = 0; i < positions.length; i++) {
                positions[i] = this.positions.get(i);
                pitch[i] = this.pitch.get(i);
                yaw[i] = this.yaw.get(i);
            }
            return new RecordedPath(positions, pitch, yaw);
        }
    }
}
