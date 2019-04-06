package com.raphydaphy.cutsceneapi.fakeworld.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.storage.RegionFile;

import java.io.*;

public class CutsceneWorldStorage implements AutoCloseable
{
	private File directory;
	private RegionFile regionFile;

	public CutsceneWorldStorage(String directory)
	{
		this.directory = new File(directory);
	}

	private RegionFile getRegionFile() throws IOException
	{
		if (this.regionFile != null)
		{
			return this.regionFile;
		} else
		{
			if (!this.directory.exists())
			{
				this.directory.mkdirs();
			}

			File file = new File(this.directory, "cutscene_chunks.mca");
			this.regionFile = new RegionFile(file);
			return regionFile;
		}
	}

	public CompoundTag getChunkData(ChunkPos pos) throws IOException
	{
		RegionFile regionFile = getRegionFile();
		if (regionFile.hasChunk(pos))
		{
			DataInputStream inputStream = regionFile.getChunkDataInputStream(pos);
			Throwable exception = null;

			CompoundTag tag;
			try
			{
				if (inputStream != null)
				{
					return NbtIo.read(inputStream);
				}

				tag = new CompoundTag();
			} catch (Throwable e)
			{
				exception = e;
				throw e;
			} finally
			{
				handleException(inputStream, exception);
			}

			return tag;
		} else
		{
			return new CompoundTag();
		}
	}

	public void setChunkData(ChunkPos chunkPos, CompoundTag chunkData) throws IOException
	{
		RegionFile regionFile = this.getRegionFile();
		DataOutputStream outputStream = regionFile.getChunkDataOutputStream(chunkPos);
		Throwable exception = null;
		try
		{
			NbtIo.write(chunkData, outputStream);
		} catch (Throwable var14)
		{
			exception = var14;
			throw var14;
		} finally
		{
			handleException(outputStream, exception);
		}
	}

	private void handleException(Closeable closable, Throwable exception) throws IOException
	{
		if (closable != null)
		{
			if (exception != null)
			{
				try
				{
					closable.close();
				} catch (Throwable var15)
				{
					exception.addSuppressed(var15);
				}
			} else
			{
				closable.close();
			}
		}
	}

	@Override
	public void close() throws IOException {
		if (regionFile != null)
		{
			regionFile.close();
		}
	}
}
