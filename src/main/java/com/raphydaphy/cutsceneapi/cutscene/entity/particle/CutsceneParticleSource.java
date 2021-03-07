package com.raphydaphy.cutsceneapi.cutscene.entity.particle;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.entity.CutsceneEntity;
import com.raphydaphy.shaded.org.joml.Vector3d;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Random;

public class CutsceneParticleSource implements CutsceneEntity {
  private final CutsceneParticleManager particleManager;
  private Identifier particleType;
  private Vector3d pos;
  private Vector3d velocityMultiplier;

  private final Random rand;

  public CutsceneParticleSource(CutsceneParticleManager particleManager, Identifier particleType, Vector3d pos, Vector3d velocityMultiplier) {
    this.particleManager = particleManager;
    this.particleType = particleType;
    this.pos = pos;
    this.velocityMultiplier = velocityMultiplier;
    this.rand = new Random();
  }

  @Override
  public void update() {
    ParticleType<? extends ParticleEffect> particleType = Registry.PARTICLE_TYPE.get(this.particleType);
    if (particleType == null) {
      CutsceneAPI.LOGGER.warn("Invalid particle type: " + this.particleType);
      return;
    }

    if (particleType instanceof DefaultParticleType) {
      this.particleManager.addParticle(
        (DefaultParticleType)particleType,
        this.pos.x, this.pos.y, this.pos.z,
        (this.rand.nextDouble() - 0.5) * this.velocityMultiplier.x,
        (this.rand.nextDouble() - 0.5) * this.velocityMultiplier.y,
        (this.rand.nextDouble() - 0.5) * this.velocityMultiplier.z
      );
    } else {
      CutsceneAPI.LOGGER.warn("Unsupported particle type: " + this.particleType);
    }
  }

  public void setParticleType(Identifier id) {
    this.particleType = id;
  }

  public void setPos(Vector3d pos) {
    this.pos = pos;
  }

  public Vector3d getPos() {
    return this.pos;
  }

  public void setVelocityMultiplier(Vector3d velocityMultiplier) {
    this.velocityMultiplier = velocityMultiplier;
  }

  public Vector3d getVelocityMultiplier() {
    return this.velocityMultiplier;
  }

  public Identifier getParticleType() {
    return this.particleType;
  }
}
