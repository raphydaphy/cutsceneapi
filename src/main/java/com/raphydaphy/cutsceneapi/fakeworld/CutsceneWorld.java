package com.raphydaphy.cutsceneapi.fakeworld;

import com.raphydaphy.cutsceneapi.mixin.client.ClientWorldHooks;
import com.raphydaphy.cutsceneapi.mixin.client.WorldHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.GameMode;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.chunk.*;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGenerator;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class CutsceneWorld extends ClientWorld {
    private final Consumer<CutsceneChunk> chunkGenCallback;
    public ClientWorld realWorld;
    public boolean cloneExisting = false;
    public long cutsceneTime;
    private Map<ChunkPos, CutsceneChunk> chunkMap = new HashMap<>();
    private CutsceneChunkManager cutsceneChunkManager;

    public CutsceneWorld(MinecraftClient client, ClientWorld realWorld, Consumer<CutsceneChunk> chunkGenCallback, boolean cloneExisting) {
        this(((ClientWorldHooks) realWorld).getCutsceneNetHandler(), new LevelInfo(realWorld.getLevelProperties()), DimensionType.OVERWORLD, 1, client.getProfiler(), client.worldRenderer, chunkGenCallback);
        this.realWorld = realWorld;
        this.cloneExisting = cloneExisting;
    }

    public CutsceneWorld(ClientPlayNetworkHandler netHandler, LevelInfo levelInfo, DimensionType dimension, int id, Profiler profiler, WorldRenderer renderer, Consumer<CutsceneChunk> chunkGenCallback) {
        super(netHandler, levelInfo, dimension, id, profiler, renderer);
        cutsceneChunkManager = new CutsceneChunkManager(this);
        this.chunkGenCallback = chunkGenCallback;
    }

    public static CutsceneWorld createCached(long seed, int radius, boolean structures, Consumer<CutsceneChunk> chunkGenCallback) {
        LevelInfo levelInfo = new LevelInfo(seed, GameMode.SPECTATOR, structures, false, LevelGeneratorType.DEFAULT);
        CutsceneWorld cutsceneWorld = new CutsceneWorld(null, levelInfo, DimensionType.OVERWORLD, 1, null, null, chunkGenCallback);

        // Configurations
        LevelProperties levelProperties = new LevelProperties(levelInfo, "Generated Cutscene World");
        OverworldChunkGeneratorConfig chunkGenConfig = new OverworldChunkGeneratorConfig();
        VanillaLayeredBiomeSourceConfig biomeConfig = new VanillaLayeredBiomeSourceConfig();

        // Apply configurations
        biomeConfig.setLevelProperties(levelProperties);
        biomeConfig.setGeneratorSettings(chunkGenConfig);

        // Biome & Chunk Generators
        BiomeSource biomeSource = new VanillaLayeredBiomeSource(biomeConfig);
        ChunkGenerator generator = new OverworldChunkGenerator(cutsceneWorld, biomeSource, chunkGenConfig);

        int cX, cZ, pX, pY, pZ, index;

        for (cX = -radius; cX < radius; cX++) {
            for (cZ = -radius; cZ < radius; cZ++) {
                if (CutsceneChunkManager.isWithinDistanceCutscene(cZ, cX, 0, 0, radius)) {
                    ChunkPos chunkPos = new ChunkPos(cX, cZ);

                    // Step 1: Create Chunk
                    ProtoChunk protoChunk = new ProtoChunk(chunkPos, new UpgradeData(new CompoundTag()));

                    // Step 2: Generate Biomes
                    generator.populateBiomes(protoChunk);

                    // Step 3: Populate Noise
                    generator.populateNoise(cutsceneWorld, protoChunk);

                    // Step 4: Build Surface
                    generator.buildSurface(protoChunk);

                    // Step 5: Carve
                    generator.carve(protoChunk, GenerationStep.Carver.AIR);
                    generator.carve(protoChunk, GenerationStep.Carver.LIQUID);

                    // Step 6: Populate Heightmaps
                    Heightmap.populateHeightmaps(protoChunk, EnumSet.of(Heightmap.Type.MOTION_BLOCKING, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Heightmap.Type.OCEAN_FLOOR, Heightmap.Type.WORLD_SURFACE));

                    // Step 7: Generate features
                    //generator.generateFeatures(new ChunkRegion(MUSHROOM_ISLAND_WORLD, Collections.singletonList(protoChunk)));

                    // Create Cutscene Chunk
                    CutsceneChunk cutsceneChunk = new CutsceneChunk(cutsceneWorld, chunkPos, protoChunk.getBiomeArray());
                    BlockState[] states = cutsceneChunk.blockStates;

                    // Transfer data to cutscene chunk
                    for (pX = 0; pX < 16; pX++) {
                        for (pY = 0; pY < cutsceneChunk.getHeight(); pY++) {
                            for (pZ = 0; pZ < 16; pZ++) {
                                index = pZ * 16 * cutsceneChunk.getHeight() + pY * 16 + pX;
                                states[index] = protoChunk.getBlockState(new BlockPos(pX, pY, pZ));
                            }
                        }
                    }

                    // Save cutscene chunk to world
                    cutsceneWorld.putChunk(chunkPos, cutsceneChunk);
                }
            }
        }

        return cutsceneWorld;
    }

    public void setupFrom(ClientWorld other) {
        ((ClientWorldHooks) this).setCutsceneNetHandler(((ClientWorldHooks) other).getCutsceneNetHandler());
        ((ClientWorldHooks) this).setWorldRenderer(MinecraftClient.getInstance().worldRenderer);
        ((WorldHooks) this).setCutsceneProfiler(other.getProfiler());
        this.realWorld = other;
    }

    public Consumer<CutsceneChunk> getChunkGenCallback() {
        return chunkGenCallback;
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus status, boolean boolean_1) {
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        if (chunkMap.containsKey(pos)) return chunkMap.get(pos);
        Biome[] biomes = new Biome[16 * 16];
        if (cloneExisting && realWorld != null) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    biomes[x * 16 + z] = realWorld.getBiome(new BlockPos(chunkX * 16 + x, 0, chunkZ * 16 + z));
                }
            }
        } else {
            Arrays.fill(biomes, Biomes.PLAINS);
        }
        CutsceneChunk chunk = new CutsceneChunk(this, new ChunkPos(chunkX, chunkZ), biomes);
        chunkMap.put(pos, chunk);
        return chunk;
    }

    public void putChunk(ChunkPos pos, CutsceneChunk chunk) {
        chunkMap.put(pos, chunk);
    }

    public void addPlayer(ClientPlayerEntity player) {
        getPlayers().add(player);
        ((ClientWorldHooks) this).getCutsceneEntities().add(player);
    }

    @Override
    public void tickEntities() {
        super.tickEntities();
    }

    @Override
    public ClientChunkManager method_2935() {
        return cutsceneChunkManager;
    }

    @Override
    public ChunkManager getChunkManager() {
        return cutsceneChunkManager;
    }

    @Override
    public void tick(BooleanSupplier booleanSupplier_1) {
        this.getWorldBorder().tick();
        this.tickTime();
        this.getProfiler().push("blocks");
        this.cutsceneChunkManager.tick(booleanSupplier_1);
        ((ClientWorldHooks) this).updateCutsceneLighting();
        this.getProfiler().pop();
        cutsceneTime++;
    }

    @Override
    public float getMoonSize() {
        return Dimension.MOON_PHASE_TO_SIZE[this.getDimension().getMoonPhase(getTimeOfDay())];
    }

    public int getMoonPhase() {
        return this.getDimension().getMoonPhase(getTimeOfDay());
    }

    @Override
    public float getSkyAngle(float float_1) {
        return this.getDimension().getSkyAngle(getTimeOfDay(), float_1);
    }

    @Override
    public long getTimeOfDay() {
        return cutsceneTime;
    }
}
