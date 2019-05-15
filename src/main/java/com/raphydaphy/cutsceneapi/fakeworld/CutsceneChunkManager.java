package com.raphydaphy.cutsceneapi.fakeworld;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneCameraEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;

@Environment(EnvType.CLIENT)
public class CutsceneChunkManager extends ClientChunkManager {
    private final MinecraftClient cutsceneClient = MinecraftClient.getInstance();
    private final WorldChunk cutsceneEmptyChunk;
    private final LightingProvider cutsceneLightingPRovider;
    private final CutsceneWorld cutsceneWorld;
    private volatile CutsceneChunkMap cutsceneChunkMap = new CutsceneChunkMap(3);
    private int cutsceneLoadedChunkCount;
    private volatile int cutscenePlayerChunkX;
    private volatile int cutscenePlayerChunkZ;

    CutsceneChunkManager(CutsceneWorld cutsceneWorld) {
        super(cutsceneWorld, 1);
        this.cutsceneWorld = cutsceneWorld;
        this.cutsceneEmptyChunk = new EmptyChunk(cutsceneWorld, new ChunkPos(0, 0));
        this.cutsceneLightingPRovider = new LightingProvider(this, true, cutsceneWorld.getDimension().hasSkyLight());
    }

    public static boolean isWithinDistanceCutscene(int chunkZ, int chunkX, int cameraX, int cameraZ, int viewDist) {
        return Math.abs(chunkZ - cameraX) <= viewDist && Math.abs(chunkX - cameraZ) <= viewDist;
    }

    @Override
    public LightingProvider getLightingProvider() {
        return this.cutsceneLightingPRovider;
    }

    @Override
    public void unload(int int_1, int int_2) {
        if (this.cutsceneChunkMap.hasChunk(int_1, int_2)) {
            this.cutsceneChunkMap.unload(this.cutsceneChunkMap.index(int_1, int_2), null);
        }
    }

    @Override
    public WorldChunk method_2857(int int_1, int int_2, ChunkStatus chunkStatus_1, boolean boolean_1) {
        WorldChunk worldChunk_1 = this.cutsceneChunkMap.getChunk(int_1, int_2);
        if (worldChunk_1 != null) {
            return worldChunk_1;
        } else {
            return boolean_1 ? this.cutsceneEmptyChunk : null;
        }
    }

    @Override
    public BlockView getWorld() {
        return this.cutsceneWorld;
    }

    @Override
    public WorldChunk loadChunkFromPacket(World world, int int_1, int int_2, PacketByteBuf packetByteBuf_1, CompoundTag compoundTag_1, int int_3, boolean boolean_1) {
        if (world instanceof CutsceneWorld) {
            return world.getWorldChunk(new BlockPos(int_1 * 16, 0, int_2 * 16));
        }
        return null;
    }

    @Override
    public void tick(BooleanSupplier booleanSupplier_1) {
        this.updateCutsceneChunkList();
    }

    private void updateCutsceneChunkList() {
        int loadDistance = this.cutsceneChunkMap.loadDistance;
        int adjustedViewDist = Math.max(2, this.cutsceneClient.options.viewDistance + -2) + 2;
        int cameraZ;
        int chunkX;
        if (loadDistance != adjustedViewDist) {
            CutsceneChunkMap chunkMap = new CutsceneChunkMap(adjustedViewDist);

            for (cameraZ = this.cutscenePlayerChunkZ - loadDistance; cameraZ <= this.cutscenePlayerChunkZ + loadDistance; ++cameraZ) {
                for (chunkX = this.cutscenePlayerChunkX - loadDistance; chunkX <= this.cutscenePlayerChunkX + loadDistance; ++chunkX) {
                    CutsceneChunk chunk = this.cutsceneChunkMap.chunks.get(this.cutsceneChunkMap.index(chunkX, cameraZ));
                    if (chunk != null) {
                        if (!isWithinDistanceCutscene(chunkX, cameraZ, this.cutscenePlayerChunkX, this.cutscenePlayerChunkZ, adjustedViewDist)) {
                            --this.cutsceneLoadedChunkCount;
                        } else {
                            chunkMap.chunks.set(chunkMap.index(chunkX, cameraZ), chunk);
                        }
                    }
                }
            }

            this.cutsceneChunkMap = chunkMap;
        }

        Entity cameraEntity = this.cutsceneClient.cameraEntity;
        int cameraX = MathHelper.floor(cameraEntity instanceof CutsceneCameraEntity ? cameraEntity.x : this.cutsceneClient.player.x) >> 4;
        cameraZ = MathHelper.floor(cameraEntity instanceof CutsceneCameraEntity ? cameraEntity.z : this.cutsceneClient.player.z) >> 4;
        if (this.cutscenePlayerChunkX != cameraX || this.cutscenePlayerChunkZ != cameraZ) {
            for (chunkX = this.cutscenePlayerChunkZ - adjustedViewDist; chunkX <= this.cutscenePlayerChunkZ + adjustedViewDist; ++chunkX) {
                for (int chunkZ = this.cutscenePlayerChunkX - adjustedViewDist; chunkZ <= this.cutscenePlayerChunkX + adjustedViewDist; ++chunkZ) {
                    if (!isWithinDistanceCutscene(chunkZ, chunkX, cameraX, cameraZ, adjustedViewDist)) {
                        this.cutsceneChunkMap.unload(this.cutsceneChunkMap.index(chunkZ, chunkX), null);
                    }
                }
            }

            this.cutscenePlayerChunkX = cameraX;
            this.cutscenePlayerChunkZ = cameraZ;
        }

    }

    @Override
    public String getStatus() {
        return "CutsceneChunkCache: " + this.cutsceneChunkMap.chunks.length() + ", " + this.cutsceneLoadedChunkCount;
    }

    @Override
    public ChunkGenerator<?> getChunkGenerator() {
        return null;
    }

    @Override
    public void onLightUpdate(LightType lightType_1, ChunkSectionPos chunkSectionPos_1) {
        MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(chunkSectionPos_1.getChunkX(), chunkSectionPos_1.getChunkY(), chunkSectionPos_1.getChunkZ());
    }

    @Override
    public Chunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
        return this.method_2857(var1, var2, var3, var4);
    }

    @Environment(EnvType.CLIENT)
    final class CutsceneChunkMap {
        private final AtomicReferenceArray<CutsceneChunk> chunks;
        private final int loadDistance;
        private final int loadDiameter;

        private CutsceneChunkMap(int int_1) {
            this.loadDistance = int_1;
            this.loadDiameter = int_1 * 2 + 1;
            this.chunks = new AtomicReferenceArray<>(this.loadDiameter * this.loadDiameter);
        }

        private int index(int int_1, int int_2) {
            return Math.floorMod(int_2, this.loadDiameter) * this.loadDiameter + Math.floorMod(int_1, this.loadDiameter);
        }

        void unload(int int_1, CutsceneChunk chunk) {
            CutsceneChunk otherChunk = this.chunks.getAndSet(int_1, chunk);
            if (otherChunk != null) {
                CutsceneChunkManager.this.cutsceneLoadedChunkCount--;
                CutsceneChunkManager.this.cutsceneWorld.unloadBlockEntities(otherChunk);
            }

        }

        private boolean hasChunk(int int_1, int int_2) {
            return CutsceneChunkManager.isWithinDistanceCutscene(int_1, int_2, CutsceneChunkManager.this.cutscenePlayerChunkX, CutsceneChunkManager.this.cutscenePlayerChunkZ, this.loadDistance);
        }

        WorldChunk getChunk(int int_1, int int_2) {
            return this.hasChunk(int_1, int_2) ? this.chunks.get(this.index(int_1, int_2)) : null;
        }
    }
}
