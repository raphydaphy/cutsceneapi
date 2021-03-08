package com.raphydaphy.cutsceneapi.cutscene.object.entity;

import com.raphydaphy.cutsceneapi.cutscene.object.CutsceneObject;
import com.raphydaphy.shaded.org.joml.Vector3d;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class CutsceneEntity implements CutsceneObject {
  private final ClientWorld world;
  private Entity entity;

  public CutsceneEntity(ClientWorld world, Entity entity) {
    this.world = world;
    this.entity = entity;

    world.addEntity(entity.getId(), entity);
  }

  @Override
  public void update() {
    this.entity.tick();
  }

  public Entity getEntity() {
    return this.entity;
  }

  public void onRemoved() {
    this.world.removeEntity(this.entity.getId(), Entity.RemovalReason.DISCARDED);
  }
}
