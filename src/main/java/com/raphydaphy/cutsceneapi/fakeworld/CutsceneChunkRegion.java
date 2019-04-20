package com.raphydaphy.cutsceneapi.fakeworld;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.ChunkStatus.ChunkType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.Heightmap.Type;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class CutsceneChunkRegion implements IWorld {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Chunk> chunks;
    private final int centerChunkX;
    private final int centerChunkZ;
    private final int width;
    private final CutsceneWorld cutsceneWorld;
    private final long seed;
    private final int seaLevel;
    private final LevelProperties levelProperties;
    private final Random random;
    private final Dimension dimension;
    private final ChunkGeneratorConfig generatorSettings;
    private final TickScheduler<Block> blockTickScheduler = new MultiTickScheduler<>((pos) -> this.getChunk(pos).getBlockTickScheduler());
    private final TickScheduler<Fluid> fluidTickScheduler = new MultiTickScheduler<>((pos) -> this.getChunk(pos).getFluidTickScheduler());

    public CutsceneChunkRegion(CutsceneWorld cutsceneWorld, List<Chunk> list_1, ChunkGeneratorConfig chunkGenConfig) {
        int int_1 = MathHelper.floor(Math.sqrt((double) list_1.size()));
        if (int_1 * int_1 != list_1.size()) {
            throw new IllegalStateException("Cache size is not a square.");
        } else {
            ChunkPos chunkPos_1 = ((Chunk) list_1.get(list_1.size() / 2)).getPos();
            this.chunks = list_1;
            this.centerChunkX = chunkPos_1.x;
            this.centerChunkZ = chunkPos_1.z;
            this.width = int_1;
            this.cutsceneWorld = cutsceneWorld;
            this.seed = cutsceneWorld.getSeed();
            this.generatorSettings = chunkGenConfig;
            this.seaLevel = cutsceneWorld.getSeaLevel();
            this.levelProperties = cutsceneWorld.getLevelProperties();
            this.random = cutsceneWorld.getRandom();
            this.dimension = cutsceneWorld.getDimension();
        }
    }

    public int getCenterChunkX() {
        return this.centerChunkX;
    }

    public int getCenterChunkZ() {
        return this.centerChunkZ;
    }

    @Override
    public Chunk getChunk(int int_1, int int_2) {
        return this.getChunk(int_1, int_2, ChunkStatus.EMPTY);
    }

    @Override
    public Chunk getChunk(int int_1, int int_2, ChunkStatus chunkStatus_1, boolean boolean_1) {
        Chunk chunk_2;
        if (this.isChunkLoaded(int_1, int_2)) {
            ChunkPos chunkPos_1 = ((Chunk) this.chunks.get(0)).getPos();
            int int_3 = int_1 - chunkPos_1.x;
            int int_4 = int_2 - chunkPos_1.z;
            chunk_2 = (Chunk) this.chunks.get(int_3 + int_4 * this.width);
            if (chunk_2.getStatus().isAtLeast(chunkStatus_1)) {
                return chunk_2;
            }
        } else {
            chunk_2 = null;
        }

        if (!boolean_1) {
            return null;
        } else {
            Chunk chunk_3 = (Chunk) this.chunks.get(0);
            Chunk chunk_4 = (Chunk) this.chunks.get(this.chunks.size() - 1);
            LOGGER.error("Requested chunk : {} {}", int_1, int_2);
            LOGGER.error("Region bounds : {} {} | {} {}", chunk_3.getPos().x, chunk_3.getPos().z, chunk_4.getPos().x, chunk_4.getPos().z);
            if (chunk_2 != null) {
                throw new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", chunkStatus_1, chunk_2.getStatus(), int_1, int_2));
            } else {
                throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", int_1, int_2));
            }
        }
    }

    @Override
    public boolean isChunkLoaded(int int_1, int int_2) {
        Chunk chunk_1 = (Chunk) this.chunks.get(0);
        Chunk chunk_2 = (Chunk) this.chunks.get(this.chunks.size() - 1);
        return int_1 >= chunk_1.getPos().x && int_1 <= chunk_2.getPos().x && int_2 >= chunk_1.getPos().z && int_2 <= chunk_2.getPos().z;
    }

    @Override
    public BlockState getBlockState(BlockPos blockPos_1) {
        return this.getChunk(blockPos_1.getX() >> 4, blockPos_1.getZ() >> 4).getBlockState(blockPos_1);
    }

    @Override
    public FluidState getFluidState(BlockPos blockPos_1) {
        return this.getChunk(blockPos_1).getFluidState(blockPos_1);
    }

    @Override
    public PlayerEntity getClosestPlayer(double double_1, double double_2, double double_3, double double_4, Predicate<Entity> predicate_1) {
        return null;
    }

    @Override
    public int getAmbientDarkness() {
        return 0;
    }

    @Override
    public Biome getBiome(BlockPos blockPos_1) {
        Biome biome_1 = this.getChunk(blockPos_1).getBiomeArray()[blockPos_1.getX() & 15 | (blockPos_1.getZ() & 15) << 4];
        if (biome_1 == null) {
            throw new RuntimeException(String.format("Biome is null @ %s", blockPos_1));
        } else {
            return biome_1;
        }
    }

    @Override
    public int getLightLevel(LightType lightType_1, BlockPos blockPos_1) {
        return this.getChunkManager().getLightingProvider().get(lightType_1).getLightLevel(blockPos_1);
    }

    @Override
    public int getLightLevel(BlockPos blockPos_1, int int_1) {
        return this.getChunk(blockPos_1).getLightLevel(blockPos_1, int_1, this.getDimension().hasSkyLight());
    }

    @Override
    public boolean breakBlock(BlockPos blockPos_1, boolean boolean_1) {
        BlockState blockState_1 = this.getBlockState(blockPos_1);
        if (blockState_1.isAir()) {
            return false;
        } else {
            if (boolean_1) {
                BlockEntity blockEntity_1 = blockState_1.getBlock().hasBlockEntity() ? this.getBlockEntity(blockPos_1) : null;
                Block.dropStacks(blockState_1, this.cutsceneWorld, blockPos_1, blockEntity_1);
            }

            return this.setBlockState(blockPos_1, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public BlockEntity getBlockEntity(BlockPos blockPos_1) {
        Chunk chunk_1 = this.getChunk(blockPos_1);
        BlockEntity blockEntity_1 = chunk_1.getBlockEntity(blockPos_1);
        if (blockEntity_1 != null) {
            return blockEntity_1;
        } else {
            CompoundTag compoundTag_1 = chunk_1.getBlockEntityTagAt(blockPos_1);
            if (compoundTag_1 != null) {
                if ("DUMMY".equals(compoundTag_1.getString("id"))) {
                    Block block_1 = this.getBlockState(blockPos_1).getBlock();
                    if (!(block_1 instanceof BlockEntityProvider)) {
                        return null;
                    }

                    blockEntity_1 = ((BlockEntityProvider) block_1).createBlockEntity(this.cutsceneWorld);
                } else {
                    blockEntity_1 = BlockEntity.createFromTag(compoundTag_1);
                }

                if (blockEntity_1 != null) {
                    chunk_1.setBlockEntity(blockPos_1, blockEntity_1);
                    return blockEntity_1;
                }
            }

            if (chunk_1.getBlockState(blockPos_1).getBlock() instanceof BlockEntityProvider) {
                LOGGER.warn("Tried to access a block entity before it was created. {}", blockPos_1);
            }

            return null;
        }
    }

    @Override
    public boolean setBlockState(BlockPos blockPos_1, BlockState blockState_1, int int_1) {
        Chunk chunk_1 = this.getChunk(blockPos_1);
        BlockState blockState_2 = chunk_1.setBlockState(blockPos_1, blockState_1, false);
        if (blockState_2 != null) {
            this.cutsceneWorld.onBlockChanged(blockPos_1, blockState_2, blockState_1);
        }

        Block block_1 = blockState_1.getBlock();
        if (block_1.hasBlockEntity()) {
            if (chunk_1.getStatus().getChunkType() == ChunkType.LEVELCHUNK) {
                chunk_1.setBlockEntity(blockPos_1, ((BlockEntityProvider) block_1).createBlockEntity(this));
            } else {
                CompoundTag compoundTag_1 = new CompoundTag();
                compoundTag_1.putInt("x", blockPos_1.getX());
                compoundTag_1.putInt("y", blockPos_1.getY());
                compoundTag_1.putInt("z", blockPos_1.getZ());
                compoundTag_1.putString("id", "DUMMY");
                chunk_1.addPendingBlockEntityTag(compoundTag_1);
            }
        } else if (blockState_2 != null && blockState_2.getBlock().hasBlockEntity()) {
            chunk_1.removeBlockEntity(blockPos_1);
        }

        if (blockState_1.shouldPostProcess(this, blockPos_1)) {
            this.markBlockForPostProcessing(blockPos_1);
        }

        return true;
    }

    private void markBlockForPostProcessing(BlockPos blockPos_1) {
        this.getChunk(blockPos_1).markBlockForPostProcessing(blockPos_1);
    }

    @Override
    public boolean spawnEntity(Entity entity_1) {
        int int_1 = MathHelper.floor(entity_1.x / 16.0D);
        int int_2 = MathHelper.floor(entity_1.z / 16.0D);
        this.getChunk(int_1, int_2).addEntity(entity_1);
        return true;
    }

    @Override
    public boolean clearBlockState(BlockPos blockPos_1, boolean boolean_1) {
        return this.setBlockState(blockPos_1, Blocks.AIR.getDefaultState(), 3);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.cutsceneWorld.getWorldBorder();
    }

    @Override
    public boolean intersectsEntities(Entity entity_1, VoxelShape voxelShape_1) {
        return true;
    }

    @Override
    public int getEmittedStrongRedstonePower(BlockPos blockPos_1, Direction direction_1) {
        return this.getBlockState(blockPos_1).getStrongRedstonePower(this, blockPos_1, direction_1);
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public LevelProperties getLevelProperties() {
        return this.levelProperties;
    }

    @Override
    public LocalDifficulty getLocalDifficulty(BlockPos blockPos_1) {
        if (!this.isChunkLoaded(blockPos_1.getX() >> 4, blockPos_1.getZ() >> 4)) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        } else {
            return new LocalDifficulty(this.cutsceneWorld.getDifficulty(), this.cutsceneWorld.getTimeOfDay(), 0L, this.cutsceneWorld.getMoonSize());
        }
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.cutsceneWorld.getChunkManager();
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public TickScheduler<Block> getBlockTickScheduler() {
        return this.blockTickScheduler;
    }

    @Override
    public TickScheduler<Fluid> getFluidTickScheduler() {
        return this.fluidTickScheduler;
    }

    @Override
    public int getSeaLevel() {
        return this.seaLevel;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public void updateNeighbors(BlockPos blockPos_1, Block block_1) {
    }

    @Override
    public int getTop(Type heightmap$Type_1, int int_1, int int_2) {
        return this.getChunk(int_1 >> 4, int_2 >> 4).sampleHeightmap(heightmap$Type_1, int_1 & 15, int_2 & 15) + 1;
    }

    @Override
    public void playSound(PlayerEntity playerEntity_1, BlockPos blockPos_1, SoundEvent soundEvent_1, SoundCategory soundCategory_1, float float_1, float float_2) {
    }

    @Override
    public void addParticle(ParticleParameters particleParameters_1, double double_1, double double_2, double double_3, double double_4, double double_5, double double_6) {
    }

    @Override
    public void playLevelEvent(PlayerEntity playerEntity_1, int int_1, BlockPos blockPos_1, int int_2) {
    }

    @Override
    public BlockPos getSpawnPos() {
        return this.cutsceneWorld.getSpawnPos();
    }

    @Override
    public Dimension getDimension() {
        return this.dimension;
    }

    @Override
    public boolean testBlockState(BlockPos blockPos_1, Predicate<BlockState> predicate_1) {
        return predicate_1.test(this.getBlockState(blockPos_1));
    }

    @Override
    public List<Entity> getEntities(Entity entity_1, BoundingBox boundingBox_1, Predicate<? super Entity> predicate_1) {
        return Collections.emptyList();
    }

    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> var1, BoundingBox var2, Predicate<? super T> var3) {
        return Collections.emptyList();
    }

    @Override
    public List<PlayerEntity> getPlayers() {
        return Collections.emptyList();
    }

    @Override
    public BlockPos getTopPosition(Type heightmap$Type_1, BlockPos blockPos_1) {
        return new BlockPos(blockPos_1.getX(), this.getTop(heightmap$Type_1, blockPos_1.getX(), blockPos_1.getZ()), blockPos_1.getZ());
    }

    @Override
    public World getWorld() {
        return cutsceneWorld;
    }
}
