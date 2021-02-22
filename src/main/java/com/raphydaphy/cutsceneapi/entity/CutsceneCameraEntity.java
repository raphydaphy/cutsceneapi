package com.raphydaphy.cutsceneapi.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

public class CutsceneCameraEntity extends Entity {

  public CutsceneCameraEntity(World world, double x, double y, double z) {
    this(world);
    this.setPos(x, y, z);
    this.prevX = x;
    this.prevY = y;
    this.prevZ = z;

    this.noClip = true;
  }

  public CutsceneCameraEntity(World world) {
    super(ModEntities.CUTSCENE_CAMERA_ENTITY, world);
  }

  public void rotateTo(float pitch, float yaw) {
    this.prevPitch = this.pitch;
    this.pitch = pitch;

    this.prevYaw = this.yaw;
    this.yaw = yaw;
  }

  public void update() {
    this.prevX = this.getX();
    this.prevY = this.getY();
    this.prevZ = this.getZ();

    this.prevYaw = this.yaw;
    this.prevPitch = this.pitch;
  }

  @Override
  protected void initDataTracker() {

  }

  @Override
  protected void readCustomDataFromNbt(CompoundTag tag) {

  }

  @Override
  protected void writeCustomDataToNbt(CompoundTag tag) {

  }

  @Override
  public Packet<?> createSpawnPacket() {
    return null;
  }
}
