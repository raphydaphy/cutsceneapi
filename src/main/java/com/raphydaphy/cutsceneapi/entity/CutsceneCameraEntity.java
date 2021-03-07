package com.raphydaphy.cutsceneapi.entity;

import com.raphydaphy.cutsceneapi.cutscene.track.property.TransformProperty;
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

  public void update() {
    this.prevX = this.getX();
    this.prevY = this.getY();
    this.prevZ = this.getZ();

    this.prevYaw = this.yaw;
    this.prevPitch = this.pitch;
  }

  public void setTransform(TransformProperty transform) {
    this.setPos(transform.getPos().x, transform.getPos().y, transform.getPos().z);
    this.pitch = transform.getPitch();
    this.yaw = transform.getYaw();
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
