package com.raphydaphy.cutsceneapi.cutscene.entity.particle;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.raphydaphy.cutsceneapi.mixin.client.ParticleManagerInvoker;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.class_5878;
import net.minecraft.client.particle.EmitterParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CutsceneParticleManager {
  private final ParticleManager particleManager;
  private final ClientWorld world;
  private final TextureManager textureManager;

  private final Map<ParticleTextureSheet, Queue<Particle>> particles = Maps.newIdentityHashMap();
  private final Queue<EmitterParticle> emitterParticles = Queues.newArrayDeque();
  private final Queue<Particle> newParticles = Queues.newArrayDeque();

  private final Object2IntOpenHashMap<class_5878> trackedParticleCounts = new Object2IntOpenHashMap<>();

  public CutsceneParticleManager(ParticleManager particleManager, ClientWorld world, TextureManager textureManager) {
    this.particleManager = particleManager;
    this.world = world;
    this.textureManager = textureManager;
  }

  private ParticleManagerInvoker getParticleManagerInvoker() {
    return (ParticleManagerInvoker) this.particleManager;
  }

  @Nullable
  public Particle addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    Particle particle = this.getParticleManagerInvoker().invokeCreateParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    if (particle != null) {
      this.addParticle(particle);
      return particle;
    } else {
      return null;
    }
  }

  public void addParticle(Particle particle) {
    Optional<class_5878> particleGroup = particle.method_34019();
    if (particleGroup.isPresent()) {
      if (this.isParticleGroupFull(particleGroup.get())) {
        this.newParticles.add(particle);
        this.updateCount(particleGroup.get(), 1);
      }
    } else {
      this.newParticles.add(particle);
    }
  }

  public void tick() {
    this.particles.forEach((particleTextureSheet, queue) -> {
      this.world.getProfiler().push(particleTextureSheet.toString());
      this.tickParticles(queue);
      this.world.getProfiler().pop();
    });
    if (!this.emitterParticles.isEmpty()) {
      List<EmitterParticle> deadEmitters = Lists.newArrayList();

      for (EmitterParticle emitterParticle : this.emitterParticles) {
        emitterParticle.tick();
        if (!emitterParticle.isAlive()) {
          deadEmitters.add(emitterParticle);
        }
      }

      this.emitterParticles.removeAll(deadEmitters);
    }

    Particle particle;
    if (!this.newParticles.isEmpty()) {
      while((particle = this.newParticles.poll()) != null) {
        (this.particles.computeIfAbsent(particle.getType(), (particleTextureSheet) -> EvictingQueue.create(16384))).add(particle);
      }
    }

  }

  private void tickParticles(Collection<Particle> collection) {
    if (!collection.isEmpty()) {
      Iterator<Particle> iterator = collection.iterator();

      while(iterator.hasNext()) {
        Particle particle = iterator.next();
        this.tickParticle(particle);
        if (!particle.isAlive()) {
          particle.method_34019().ifPresent((arg) -> this.updateCount(arg, -1));
          iterator.remove();
        }
      }
    }
  }

  private void tickParticle(Particle particle) {
    try {
      particle.tick();
    } catch (Throwable var5) {
      CrashReport crashReport = CrashReport.create(var5, "Ticking Particle");
      CrashReportSection crashReportSection = crashReport.addElement("Particle being ticked");
      crashReportSection.add("Particle", particle::toString);
      ParticleTextureSheet var10002 = particle.getType();
      crashReportSection.add("Particle Type", var10002::toString);
      throw new CrashException(crashReport);
    }
  }


  public void renderParticles(MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, LightmapTextureManager lightmapTextureManager, Camera camera, float f) {
    lightmapTextureManager.enable();
    RenderSystem.enableAlphaTest();
    RenderSystem.defaultAlphaFunc();
    RenderSystem.enableDepthTest();
    RenderSystem.enableFog();
    RenderSystem.pushMatrix();
    RenderSystem.multMatrix(matrixStack.peek().getModel());
    Iterator<ParticleTextureSheet> textureSheetIterator = ParticleManagerInvoker.getParticleTextureSheets().iterator();

    while(true) {
      ParticleTextureSheet particleTextureSheet;
      Iterable<Particle> particleQueue;
      do {
        if (!textureSheetIterator.hasNext()) {
          RenderSystem.popMatrix();
          RenderSystem.depthMask(true);
          RenderSystem.depthFunc(515);
          RenderSystem.disableBlend();
          RenderSystem.defaultAlphaFunc();
          lightmapTextureManager.disable();
          RenderSystem.disableFog();
          return;
        }

        particleTextureSheet = textureSheetIterator.next();
        particleQueue = this.particles.get(particleTextureSheet);
      } while(particleQueue == null);

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferBuilder = tessellator.getBuffer();
      particleTextureSheet.begin(bufferBuilder, this.textureManager);

      for (Particle particle : particleQueue) {
        try {
          particle.buildGeometry(bufferBuilder, camera, f);
        } catch (Throwable var16) {
          CrashReport crashReport = CrashReport.create(var16, "Rendering Particle");
          CrashReportSection crashReportSection = crashReport.addElement("Particle being rendered");
          crashReportSection.add("Particle", particle::toString);
          crashReportSection.add("Particle Type", particleTextureSheet::toString);
          throw new CrashException(crashReport);
        }
      }

      particleTextureSheet.draw(tessellator);
    }
  }

  private void updateCount(class_5878 particleGroup, int amount) {
    this.trackedParticleCounts.addTo(particleGroup, amount);
  }

  private boolean isParticleGroupFull(class_5878 particleGroup) {
    return this.trackedParticleCounts.getInt(particleGroup) < particleGroup.method_34045();
  }
}