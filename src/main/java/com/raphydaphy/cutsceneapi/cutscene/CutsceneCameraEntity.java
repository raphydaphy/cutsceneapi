package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class CutsceneCameraEntity extends Entity {
    public CutsceneCameraEntity(World world) {
        super(CutsceneAPI.CUTSCENE_CAMERA_ENTITY, world);
    }

    CutsceneCameraEntity withPos(Vector3f pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        return this;
    }

    void moveTo(Vector3f pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    void update() {
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag var1) {
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag var1) {
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }
}
