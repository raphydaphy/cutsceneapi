package com.raphydaphy.cutsceneapi.mixin.client;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.particle.ParticleEffect;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ParticleManager.class)
public interface ParticleManagerInvoker {
  @Accessor("PARTICLE_TEXTURE_SHEETS")
  static List<ParticleTextureSheet> getParticleTextureSheets() {
    throw new AssertionError();
  }

  @Invoker("createParticle")
  @Nullable <T extends ParticleEffect> Particle invokeCreateParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
}
