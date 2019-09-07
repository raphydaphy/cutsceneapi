package com.raphydaphy.cutsceneapi.fakeworld.storage;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.CutsceneAPIClient;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.*;
import net.minecraft.server.world.ServerTickScheduler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.GameMode;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSourceConfig;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.storage.RegionFile;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.*;

/**
 * Copied from ChunkSerializer
 * Doesn't save:
 * - Structures
 * - Tile Entities
 * - Entities
 * - Inhabited time
 * This is because cutscene chunks don't need this information
 */
public class CutsceneChunkSerializer {

    public static void serializeAndSave(World world, Chunk chunk) {
        if (!(chunk instanceof EmptyChunk)) {
            CompoundTag chunkData = serialize(world, chunk);
            try {
                CutsceneAPIClient.STORAGE.setChunkData("serialized.cworld", chunk.getPos(), chunkData);
                //saveRegion(file, chunk.getPos(), chunkData, false);
            } catch (IOException e) {
                CutsceneAPI.log(Level.ERROR, "Failed to save chunk for cutscene storage! Printing stack trace...");
                e.printStackTrace();
            }
        }
    }

    public static Chunk deserialize(World world_1, ChunkPos chunkPos, CompoundTag chunkTag) {
        // TODO: get actual biome source
        LevelInfo levelInfo = new LevelInfo(0, GameMode.SPECTATOR, false, false, LevelGeneratorType.DEFAULT);
        OverworldChunkGeneratorConfig chunkGenConfig = new OverworldChunkGeneratorConfig();
        VanillaLayeredBiomeSourceConfig biomeConfig = new VanillaLayeredBiomeSourceConfig();
        biomeConfig.setLevelProperties(new LevelProperties(levelInfo, "Deserialized Cutscene Chunk"));
        biomeConfig.setGeneratorSettings(chunkGenConfig);
        BiomeSource biomeSource_1 = new VanillaLayeredBiomeSource(biomeConfig);

        CompoundTag compoundTag_2 = chunkTag.getCompound("Level");
        ChunkPos storedPos = new ChunkPos(compoundTag_2.getInt("xPos"), compoundTag_2.getInt("zPos"));
        if (!Objects.equals(chunkPos, storedPos)) {
            CutsceneAPI.log(Level.ERROR, "Cutscene Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", chunkPos, chunkPos, storedPos);
        }

        Biome[] biomes = new Biome[256];
        BlockPos.Mutable blockPos$Mutable_1 = new BlockPos.Mutable();
        if (compoundTag_2.containsKey("Biomes", 11)) {
            int[] ints_1 = compoundTag_2.getIntArray("Biomes");

            for (int int_1 = 0; int_1 < ints_1.length; ++int_1) {
                biomes[int_1] = Registry.BIOME.get(ints_1[int_1]);
                if (biomes[int_1] == null) {
                    biomes[int_1] = biomeSource_1.getBiome(blockPos$Mutable_1.set((int_1 & 15) + chunkPos.getStartX(), 0, (int_1 >> 4 & 15) + chunkPos.getStartZ()));
                }
            }
        } else {
            for (int int_2 = 0; int_2 < biomes.length; ++int_2) {
                biomes[int_2] = biomeSource_1.getBiome(blockPos$Mutable_1.set((int_2 & 15) + chunkPos.getStartX(), 0, (int_2 >> 4 & 15) + chunkPos.getStartZ()));
            }
        }

        UpgradeData upgradeData_1 = compoundTag_2.containsKey("UpgradeData", 10) ? new UpgradeData(compoundTag_2.getCompound("UpgradeData")) : UpgradeData.NO_UPGRADE_DATA;

        ChunkTickScheduler<Block> chunkTickScheduler_1 = new ChunkTickScheduler<>((block_1) -> block_1 == null || block_1.getDefaultState().isAir(), chunkPos, compoundTag_2.getList("ToBeTicked", 9));
        ChunkTickScheduler<Fluid> chunkTickScheduler_2 = new ChunkTickScheduler<>((fluid_1) -> fluid_1 == null || fluid_1 == Fluids.EMPTY, chunkPos, compoundTag_2.getList("LiquidsToBeTicked", 9));
        boolean isLightOn = compoundTag_2.getBoolean("isLightOn");
        ListTag sections = compoundTag_2.getList("Sections", 10);
        ChunkSection[] chunkSections_1 = new ChunkSection[16];
        boolean boolean_2 = world_1.getDimension().hasSkyLight();
        ChunkManager chunkManager_1 = world_1.getChunkManager();
        LightingProvider lightingProvider_1 = chunkManager_1.getLightingProvider();

        for (int int_4 = 0; int_4 < sections.size(); ++int_4) {
            CompoundTag compoundTag_3 = sections.getCompoundTag(int_4);
            int int_5 = compoundTag_3.getByte("Y");
            if (compoundTag_3.containsKey("Palette", 9) && compoundTag_3.containsKey("BlockStates", 12)) {
                ChunkSection chunkSection_1 = new ChunkSection(int_5 << 4);
                chunkSection_1.getContainer().read(compoundTag_3.getList("Palette", 10), compoundTag_3.getLongArray("BlockStates"));
                chunkSection_1.calculateCounts();
                if (!chunkSection_1.isEmpty()) {
                    chunkSections_1[int_5] = chunkSection_1;
                }
            }

            if (isLightOn) {
                if (compoundTag_3.containsKey("BlockLight", 7)) {
                    lightingProvider_1.queueData(LightType.BLOCK, ChunkSectionPos.from(chunkPos, int_5), new ChunkNibbleArray(compoundTag_3.getByteArray("BlockLight")));
                }

                if (boolean_2 && compoundTag_3.containsKey("SkyLight", 7)) {
                    lightingProvider_1.queueData(LightType.SKY, ChunkSectionPos.from(chunkPos, int_5), new ChunkNibbleArray(compoundTag_3.getByteArray("SkyLight")));
                }
            }
        }

        ChunkStatus.ChunkType chunkStatus$ChunkType_1 = getChunkType(chunkTag);
        Chunk chunk_2;
        if (chunkStatus$ChunkType_1 == ChunkStatus.ChunkType.LEVELCHUNK) {
            BlockState[] blockStates = new BlockState[16 * world_1.getHeight() * 16];

            // TODO: convert to cutscene chunk
            //chunk_2 = new CutsceneChunk(world_1, chunkPos, biomes, blockStates);
            chunk_2 = new WorldChunk(world_1.getWorld(), chunkPos, biomes, upgradeData_1, chunkTickScheduler_1, chunkTickScheduler_2, 0, chunkSections_1, (worldChunk_1) ->
            {
                writeEntities(compoundTag_2, worldChunk_1);
            });
        } else {
            ProtoChunk protoChunk_1 = new ProtoChunk(chunkPos, upgradeData_1, chunkSections_1, chunkTickScheduler_1, chunkTickScheduler_2);
            chunk_2 = protoChunk_1;
            protoChunk_1.setBiomeArray(biomes);
            protoChunk_1.setStatus(ChunkStatus.get(compoundTag_2.getString("Status")));
            if (protoChunk_1.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
                protoChunk_1.setLightingProvider(lightingProvider_1);
            }

            if (!isLightOn && protoChunk_1.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
                for (BlockPos blockPos_1 : BlockPos.iterate(chunkPos.getStartX(), 0, chunkPos.getStartZ(), chunkPos.getEndX(), 255, chunkPos.getEndZ())) {
                    if (chunk_2.getBlockState(blockPos_1).getLuminance() != 0) {
                        protoChunk_1.addLightSource(blockPos_1);
                    }
                }
            }
        }

        chunk_2.setLightOn(isLightOn);
        CompoundTag compoundTag_4 = compoundTag_2.getCompound("Heightmaps");
        EnumSet<Heightmap.Type> enumSet_1 = EnumSet.noneOf(Heightmap.Type.class);

        for (Heightmap.Type heightmapType : chunk_2.getStatus().isSurfaceGenerated()) {
            String string_1 = heightmapType.getName();
            if (compoundTag_4.containsKey(string_1, 12)) {
                chunk_2.setHeightmap(heightmapType, compoundTag_4.getLongArray(string_1));
            } else {
                enumSet_1.add(heightmapType);
            }
        }

        Heightmap.populateHeightmaps(chunk_2, enumSet_1);
        if (compoundTag_2.getBoolean("shouldSave")) {
            chunk_2.setShouldSave(true);
        }

        ListTag listTag_2 = compoundTag_2.getList("PostProcessing", 9);

        ListTag listTag_4;
        int int_8;
        for (int int_6 = 0; int_6 < listTag_2.size(); ++int_6) {
            listTag_4 = listTag_2.getListTag(int_6);

            for (int_8 = 0; int_8 < listTag_4.size(); ++int_8) {
                chunk_2.markBlockForPostProcessing(listTag_4.getShort(int_8), int_6);
            }
        }

        if (chunkStatus$ChunkType_1 == ChunkStatus.ChunkType.LEVELCHUNK) {
            return chunk_2;
        } else {
            ProtoChunk protoChunk_2 = (ProtoChunk) chunk_2;

            ListTag lights = compoundTag_2.getList("Lights", 9);

            for (int int_10 = 0; int_10 < lights.size(); ++int_10) {
                ListTag listTag_7 = lights.getListTag(int_10);

                for (int int_11 = 0; int_11 < listTag_7.size(); ++int_11) {
                    protoChunk_2.addLightSource(listTag_7.getShort(int_11), int_10);
                }
            }

            CompoundTag carvingMasks = compoundTag_2.getCompound("CarvingMasks");

            for (String key : carvingMasks.getKeys()) {
                GenerationStep.Carver generationStep$Carver_1 = GenerationStep.Carver.valueOf(key);
                protoChunk_2.setCarvingMask(generationStep$Carver_1, BitSet.valueOf(carvingMasks.getByteArray(key)));
            }

            return protoChunk_2;
        }
    }

    public static CompoundTag serialize(World world_1, Chunk chunk_1) {
        ChunkPos chunkPos_1 = chunk_1.getPos();
        CompoundTag compoundTag_1 = new CompoundTag();
        CompoundTag compoundTag_2 = new CompoundTag();
        compoundTag_1.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        compoundTag_1.put("Level", compoundTag_2);
        compoundTag_2.putInt("xPos", chunkPos_1.x);
        compoundTag_2.putInt("zPos", chunkPos_1.z);
        compoundTag_2.putLong("LastUpdate", world_1.getTime());
        compoundTag_2.putString("Status", chunk_1.getStatus().getName());
        UpgradeData upgradeData_1 = chunk_1.getUpgradeData();
        if (!upgradeData_1.method_12349()) {
            compoundTag_2.put("UpgradeData", upgradeData_1.toTag());
        }

        ChunkSection[] chunkSections_1 = chunk_1.getSectionArray();
        ListTag sections = new ListTag();
        LightingProvider lightingProvider_1 = world_1.getChunkManager().getLightingProvider();

        CompoundTag section;
        for (int int_1 = -1; int_1 < 17; ++int_1) {
            int mojangFix = int_1;
            ChunkSection chunkSection_1 = Arrays.stream(chunkSections_1).filter((chunkSection_1x) -> chunkSection_1x != null && chunkSection_1x.getYOffset() >> 4 == mojangFix).findFirst().orElse(WorldChunk.EMPTY_SECTION);
            ChunkNibbleArray chunkNibbleArray_1 = lightingProvider_1.get(LightType.BLOCK).getChunkLightArray(ChunkSectionPos.from(chunkPos_1, int_1));
            ChunkNibbleArray chunkNibbleArray_2 = lightingProvider_1.get(LightType.SKY).getChunkLightArray(ChunkSectionPos.from(chunkPos_1, int_1));
            if (chunkSection_1 != WorldChunk.EMPTY_SECTION || chunkNibbleArray_1 != null || chunkNibbleArray_2 != null) {
                section = new CompoundTag();
                section.putByte("Y", (byte) (int_1 & 255));
                if (chunkSection_1 != WorldChunk.EMPTY_SECTION) {
                    chunkSection_1.getContainer().write(section, "Palette", "BlockStates");
                }

                if (chunkNibbleArray_1 != null && !chunkNibbleArray_1.isUninitialized()) {
                    section.putByteArray("BlockLight", chunkNibbleArray_1.asByteArray());
                }

                if (chunkNibbleArray_2 != null && !chunkNibbleArray_2.isUninitialized()) {
                    section.putByteArray("SkyLight", chunkNibbleArray_2.asByteArray());
                }

                sections.add(section);
            }
        }

        compoundTag_2.put("Sections", sections);
        if (chunk_1.isLightOn()) {
            compoundTag_2.putBoolean("isLightOn", true);
        }

        Biome[] biomes = chunk_1.getBiomeArray();
        int[] ints_1 = biomes != null ? new int[biomes.length] : new int[0];
        if (biomes != null) {
            for (int int_3 = 0; int_3 < biomes.length; ++int_3) {
                ints_1[int_3] = Registry.BIOME.getRawId(biomes[int_3]);
            }
        }

        compoundTag_2.putIntArray("Biomes", ints_1);

        if (chunk_1.getStatus().getChunkType() == ChunkStatus.ChunkType.LEVELCHUNK) {
            // TODO: What is this for ?
            //WorldChunk worldChunk_1 = (WorldChunk) chunk_1;
            //worldChunk_1.method_12232(false);
        } else {
            ProtoChunk protoChunk_1 = (ProtoChunk) chunk_1;

            compoundTag_2.put("Lights", toNbt(protoChunk_1.getLightSourcesBySection()));
            section = new CompoundTag();
            GenerationStep.Carver[] var33 = GenerationStep.Carver.values();
            int var35 = var33.length;

            for (int var37 = 0; var37 < var35; ++var37) {
                GenerationStep.Carver generationStep$Carver_1 = var33[var37];
                section.putByteArray(generationStep$Carver_1.toString(), chunk_1.getCarvingMask(generationStep$Carver_1).toByteArray());
            }

            compoundTag_2.put("CarvingMasks", section);
        }

        if (world_1.getBlockTickScheduler() instanceof ServerTickScheduler) {
            compoundTag_2.put("TileTicks", ((ServerTickScheduler) world_1.getBlockTickScheduler()).toTag(chunkPos_1));
        }

        if (chunk_1.getBlockTickScheduler() instanceof ChunkTickScheduler) {
            compoundTag_2.put("ToBeTicked", ((ChunkTickScheduler) chunk_1.getBlockTickScheduler()).toNbt());
        }

        if (world_1.getFluidTickScheduler() instanceof ServerTickScheduler) {
            compoundTag_2.put("LiquidTicks", ((ServerTickScheduler) world_1.getFluidTickScheduler()).toTag(chunkPos_1));
        }

        if (chunk_1.getFluidTickScheduler() instanceof ChunkTickScheduler) {
            compoundTag_2.put("LiquidsToBeTicked", ((ChunkTickScheduler) chunk_1.getFluidTickScheduler()).toNbt());
        }

        compoundTag_2.put("PostProcessing", toNbt(chunk_1.getPostProcessingLists()));
        CompoundTag compoundTag_9 = new CompoundTag();

        for (Map.Entry<Heightmap.Type, Heightmap> map$Entry_1 : chunk_1.getHeightmaps()) {
            if (chunk_1.getStatus().isSurfaceGenerated().contains(map$Entry_1.getKey())) {
                compoundTag_9.put((map$Entry_1.getKey()).getName(), new LongArrayTag(((Heightmap) map$Entry_1.getValue()).asLongArray()));
            }
        }

        compoundTag_2.put("Heightmaps", compoundTag_9);
        return compoundTag_1;
    }

    public static ChunkStatus.ChunkType getChunkType(CompoundTag compoundTag_1) {
        if (compoundTag_1 != null) {
            ChunkStatus chunkStatus_1 = ChunkStatus.get(compoundTag_1.getCompound("Level").getString("Status"));
            if (chunkStatus_1 != null) {
                return chunkStatus_1.getChunkType();
            }
        }

        return ChunkStatus.ChunkType.PROTOCHUNK;
    }

    private static void writeEntities(CompoundTag compoundTag_1, WorldChunk worldChunk_1) {
        World world_1 = worldChunk_1.getWorld();

        // TODO: i think i need this
        if (compoundTag_1.containsKey("TileTicks", 9) && world_1.getBlockTickScheduler() instanceof ServerTickScheduler) {
            //((ServerTickScheduler) world_1.getBlockTickScheduler()).fromTag(compoundTag_1.getList("TileTicks", 10));
        }

        if (compoundTag_1.containsKey("LiquidTicks", 9) && world_1.getFluidTickScheduler() instanceof ServerTickScheduler) {
            //((ServerTickScheduler) world_1.getFluidTickScheduler()).fromTag(compoundTag_1.getList("LiquidTicks", 10));
        }
    }

    public static ListTag toNbt(ShortList[] shortLists_1) {
        ListTag listTag_1 = new ListTag();
        ShortList[] var2 = shortLists_1;
        int var3 = shortLists_1.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            ShortList shortList_1 = var2[var4];
            ListTag listTag_2 = new ListTag();
            if (shortList_1 != null) {
                ShortListIterator var7 = shortList_1.iterator();

                while (var7.hasNext()) {
                    Short short_1 = var7.nextShort();
                    listTag_2.add(new ShortTag(short_1));
                }
            }

            listTag_1.add(listTag_2);
        }

        return listTag_1;
    }
}
