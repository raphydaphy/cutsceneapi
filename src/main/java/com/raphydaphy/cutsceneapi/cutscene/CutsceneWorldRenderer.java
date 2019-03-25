package com.raphydaphy.cutsceneapi.cutscene;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class CutsceneWorldRenderer
{
	private MinecraftClient client;
	private final TextureManager textureManager;
	private double lastTranslucentSortX;
	private double lastTranslucentSortY;
	private double lastTranslucentSortZ;
	private boolean vertexBufferObjectsEnabled;
	private int renderDistance;
	private ChunkRendererList chunkRendererList;
	private ChunkBatcher chunkBatcher;
	private List<ChunkRendererDirectionSet> chunkDirectionRenderers = Lists.newArrayListWithCapacity(69696);
	private ChunkRendererFactory chunkRendererFactory;

	private boolean terrainUpdateNecessary;
	private double lastCameraChunkUpdateX = Double.MIN_VALUE;
	private double lastCameraChunkUpdateY = Double.MIN_VALUE;
	private double lastCameraChunkUpdateZ = Double.MIN_VALUE;
	private int cameraChunkX = Integer.MIN_VALUE;
	private int cameraChunkY = Integer.MIN_VALUE;
	private int cameraChunkZ = Integer.MIN_VALUE;
	private double lastCameraX = Double.MIN_VALUE;
	private double lastCameraY = Double.MIN_VALUE;
	private double lastCameraZ = Double.MIN_VALUE;
	private double lastCameraPitch = Double.MIN_VALUE;
	private double lastCameraYaw = Double.MIN_VALUE;

	public CutsceneWorldRenderer(MinecraftClient client)
	{
		this.client = client;
		this.chunkBatcher = new ChunkBatcher();
		this.chunkBatcher.method_3632();
		this.textureManager = client.getTextureManager();
		this.renderDistance = -1;
		this.terrainUpdateNecessary = true;

		this.vertexBufferObjectsEnabled = GLX.useVbo();
		if (this.vertexBufferObjectsEnabled)
		{
			this.chunkRendererList = new VboChunkRendererList();
			this.chunkRendererFactory = ChunkRenderer::new;
		} else
		{
			this.chunkRendererList = new DisplayListChunkRendererList();
			this.chunkRendererFactory = DisplayListChunkRenderer::new;
		}
	}

	public void reload()
	{
		if (this.world != null)
		{
			if (this.chunkBatcher == null)
			{
				this.chunkBatcher = new ChunkBatcher();
			}

			this.terrainUpdateNecessary = true;
			this.cloudsDirty = true;
			LeavesBlock.setRenderingMode(this.client.options.fancyGraphics);
			this.renderDistance = this.client.options.viewDistance;
			boolean boolean_1 = this.vertexBufferObjectsEnabled;
			this.vertexBufferObjectsEnabled = GLX.useVbo();
			if (boolean_1 && !this.vertexBufferObjectsEnabled)
			{
				this.chunkRendererList = new DisplayListChunkRendererList();
				this.chunkRendererFactory = DisplayListChunkRenderer::new;
			} else if (!boolean_1 && this.vertexBufferObjectsEnabled)
			{
				this.chunkRendererList = new VboChunkRendererList();
				this.chunkRendererFactory = ChunkRenderer::new;
			}

			if (boolean_1 != this.vertexBufferObjectsEnabled)
			{
				this.setupStarRendering();
				this.method_3277();
				this.method_3265();
			}

			if (this.chunkRenderDispatcher != null)
			{
				this.chunkRenderDispatcher.delete();
			}

			this.method_3280();
			synchronized (this.field_4055)
			{
				this.field_4055.clear();
			}

			this.chunkRenderDispatcher = new ChunkRenderDispatcher(this.world, this.client.options.viewDistance, this, this.chunkRendererFactory);
			if (this.world != null)
			{
				Entity entity_1 = this.client.getCameraEntity();
				if (entity_1 != null)
				{
					this.chunkRenderDispatcher.updateCameraPosition(entity_1.x, entity_1.z);
				}
			}

			this.field_4076 = 2;
		}
	}

	public void setUpTerrain(Camera camera_1, VisibleRegion visibleRegion_1, int int_1, boolean boolean_1)
	{
		if (this.client.options.viewDistance != this.renderDistance)
		{
			this.reload();
		}

		this.world.getProfiler().push("camera");
		double double_1 = this.client.player.x - this.lastCameraChunkUpdateX;
		double double_2 = this.client.player.y - this.lastCameraChunkUpdateY;
		double double_3 = this.client.player.z - this.lastCameraChunkUpdateZ;
		if (this.cameraChunkX != this.client.player.chunkX || this.cameraChunkY != this.client.player.chunkY || this.cameraChunkZ != this.client.player.chunkZ || double_1 * double_1 + double_2 * double_2 + double_3 * double_3 > 16.0D)
		{
			this.lastCameraChunkUpdateX = this.client.player.x;
			this.lastCameraChunkUpdateY = this.client.player.y;
			this.lastCameraChunkUpdateZ = this.client.player.z;
			this.cameraChunkX = this.client.player.chunkX;
			this.cameraChunkY = this.client.player.chunkY;
			this.cameraChunkZ = this.client.player.chunkZ;
			this.chunkRenderDispatcher.updateCameraPosition(this.client.player.x, this.client.player.z);
		}

		this.world.getProfiler().swap("renderlistcamera");
		this.chunkRendererList.setCameraPosition(camera_1.getPos().x, camera_1.getPos().y, camera_1.getPos().z);
		this.chunkBatcher.method_19419(camera_1.getPos());
		this.world.getProfiler().swap("cull");
		if (this.forcedFrustum != null)
		{
			FrustumWithOrigin frustumWithOrigin_1 = new FrustumWithOrigin(this.forcedFrustum);
			frustumWithOrigin_1.setOrigin(this.forcedFrustumPosition.x, this.forcedFrustumPosition.y, this.forcedFrustumPosition.z);
			visibleRegion_1 = frustumWithOrigin_1;
		}

		this.client.getProfiler().swap("culling");
		BlockPos blockPos_1 = camera_1.getBlockPos();
		ChunkRenderer chunkRenderer_1 = this.chunkRenderDispatcher.getChunk(blockPos_1);
		BlockPos blockPos_2 = new BlockPos(MathHelper.floor(camera_1.getPos().x / 16.0D) * 16, MathHelper.floor(camera_1.getPos().y / 16.0D) * 16, MathHelper.floor(camera_1.getPos().z / 16.0D) * 16);
		float float_1 = camera_1.getPitch();
		float float_2 = camera_1.getYaw();
		this.terrainUpdateNecessary = this.terrainUpdateNecessary || !this.chunkRenderers.isEmpty() || camera_1.getPos().x != this.lastCameraX || camera_1.getPos().y != this.lastCameraY || camera_1.getPos().z != this.lastCameraZ || (double) float_1 != this.lastCameraPitch || (double) float_2 != this.lastCameraYaw;
		this.lastCameraX = camera_1.getPos().x;
		this.lastCameraY = camera_1.getPos().y;
		this.lastCameraZ = camera_1.getPos().z;
		this.lastCameraPitch = (double) float_1;
		this.lastCameraYaw = (double) float_2;
		boolean boolean_2 = this.forcedFrustum != null;
		this.client.getProfiler().swap("update");
		WorldRenderer.class_762 worldRenderer$class_762_2;
		ChunkRenderer chunkRenderer_3;
		if (!boolean_2 && this.terrainUpdateNecessary)
		{
			this.terrainUpdateNecessary = false;
			this.field_4086 = Lists.newArrayList();
			Queue<WorldRenderer.class_762> queue_1 = Queues.newArrayDeque();
			Entity.setRenderDistanceMultiplier(MathHelper.clamp((double) this.client.options.viewDistance / 8.0D, 1.0D, 2.5D));
			boolean boolean_3 = this.client.field_1730;
			if (chunkRenderer_1 != null)
			{
				boolean boolean_4 = false;
				WorldRenderer.class_762 worldRenderer$class_762_1 = new WorldRenderer.class_762(chunkRenderer_1, (Direction) null, 0);
				Set<Direction> set_1 = this.method_3285(blockPos_1);
				if (set_1.size() == 1)
				{
					net.minecraft.util.math.Vec3d vec3d_1 = camera_1.method_19335();
					Direction direction_1 = Direction.getFacing(vec3d_1.x, vec3d_1.y, vec3d_1.z).getOpposite();
					set_1.remove(direction_1);
				}

				if (set_1.isEmpty())
				{
					boolean_4 = true;
				}

				if (boolean_4 && !boolean_1)
				{
					this.field_4086.add(worldRenderer$class_762_1);
				} else
				{
					if (boolean_1 && this.world.getBlockState(blockPos_1).isFullOpaque(this.world, blockPos_1))
					{
						boolean_3 = false;
					}

					chunkRenderer_1.method_3671(int_1);
					queue_1.add(worldRenderer$class_762_1);
				}
			} else
			{
				int int_2 = blockPos_1.getY() > 0 ? 248 : 8;

				for (int int_3 = -this.renderDistance; int_3 <= this.renderDistance; ++int_3)
				{
					for (int int_4 = -this.renderDistance; int_4 <= this.renderDistance; ++int_4)
					{
						ChunkRenderer chunkRenderer_2 = this.chunkRenderDispatcher.getChunk(new BlockPos((int_3 << 4) + 8, int_2, (int_4 << 4) + 8));
						if (chunkRenderer_2 != null && ((VisibleRegion) visibleRegion_1).intersects(chunkRenderer_2.boundingBox))
						{
							chunkRenderer_2.method_3671(int_1);
							queue_1.add(new WorldRenderer.class_762(chunkRenderer_2, (Direction) null, 0));
						}
					}
				}
			}

			this.client.getProfiler().push("iteration");

			while (!queue_1.isEmpty())
			{
				worldRenderer$class_762_2 = (WorldRenderer.class_762) queue_1.poll();
				chunkRenderer_3 = worldRenderer$class_762_2.field_4124;
				Direction direction_2 = worldRenderer$class_762_2.field_4125;
				this.field_4086.add(worldRenderer$class_762_2);
				Direction[] var39 = DIRECTIONS;
				int var41 = var39.length;

				for (int var24 = 0; var24 < var41; ++var24)
				{
					Direction direction_3 = var39[var24];
					ChunkRenderer chunkRenderer_4 = this.method_3241(blockPos_2, chunkRenderer_3, direction_3);
					if ((!boolean_3 || !worldRenderer$class_762_2.method_3298(direction_3.getOpposite())) && (!boolean_3 || direction_2 == null || chunkRenderer_3.getChunkRenderData().method_3650(direction_2.getOpposite(), direction_3)) && chunkRenderer_4 != null && chunkRenderer_4.method_3673() && chunkRenderer_4.method_3671(int_1) && ((VisibleRegion) visibleRegion_1).intersects(chunkRenderer_4.boundingBox))
					{
						WorldRenderer.class_762 worldRenderer$class_762_3 = new WorldRenderer.class_762(chunkRenderer_4, direction_3, worldRenderer$class_762_2.field_4122 + 1);
						worldRenderer$class_762_3.method_3299(worldRenderer$class_762_2.field_4126, direction_3);
						queue_1.add(worldRenderer$class_762_3);
					}
				}
			}

			this.client.getProfiler().pop();
		}

		this.client.getProfiler().swap("captureFrustum");
		if (this.field_4066)
		{
			this.method_3275(camera_1.getPos().x, camera_1.getPos().y, camera_1.getPos().z);
			this.field_4066 = false;
		}

		this.client.getProfiler().swap("rebuildNear");
		Set<ChunkRenderer> set_2 = this.chunkRenderers;
		this.chunkRenderers = Sets.newLinkedHashSet();
		Iterator var30 = this.field_4086.iterator();

		while (true)
		{
			while (true)
			{
				do
				{
					if (!var30.hasNext())
					{
						this.chunkRenderers.addAll(set_2);
						this.client.getProfiler().pop();
						return;
					}

					worldRenderer$class_762_2 = (WorldRenderer.class_762) var30.next();
					chunkRenderer_3 = worldRenderer$class_762_2.field_4124;
				} while (!chunkRenderer_3.method_3672() && !set_2.contains(chunkRenderer_3));

				this.terrainUpdateNecessary = true;
				BlockPos blockPos_3 = chunkRenderer_3.getOrigin().add(8, 8, 8);
				boolean boolean_5 = blockPos_3.squaredDistanceTo(blockPos_1) < 768.0D;
				if (!chunkRenderer_3.method_3661() && !boolean_5)
				{
					this.chunkRenderers.add(chunkRenderer_3);
				} else
				{
					this.client.getProfiler().push("build near");
					this.chunkBatcher.method_3627(chunkRenderer_3);
					chunkRenderer_3.method_3662();
					this.client.getProfiler().pop();
				}
			}
		}
	}

	public int renderLayer(BlockRenderLayer blockRenderLayer_1, Camera camera_1)
	{
		GuiLighting.disable();
		if (blockRenderLayer_1 == BlockRenderLayer.TRANSLUCENT)
		{
			this.client.getProfiler().push("translucent_sort");
			double double_1 = camera_1.getPos().x - this.lastTranslucentSortX;
			double double_2 = camera_1.getPos().y - this.lastTranslucentSortY;
			double double_3 = camera_1.getPos().z - this.lastTranslucentSortZ;
			if (double_1 * double_1 + double_2 * double_2 + double_3 * double_3 > 1.0D)
			{
				this.lastTranslucentSortX = camera_1.getPos().x;
				this.lastTranslucentSortY = camera_1.getPos().y;
				this.lastTranslucentSortZ = camera_1.getPos().z;
				int int_1 = 0;

				for (ChunkRendererDirectionSet chunkRendererDirectionSet : this.chunkDirectionRenderers)
				{
					if (chunkRendererDirectionSet.renderer.chunkRenderData.isBufferInitialized(blockRenderLayer_1) && int_1++ < 15)
					{
						this.chunkBatcher.method_3620(chunkRendererDirectionSet.renderer);
					}
				}
			}

			this.client.getProfiler().pop();
		}

		this.client.getProfiler().push("filterempty");
		int int_2 = 0;
		boolean renderingTranslucentLayer = blockRenderLayer_1 == BlockRenderLayer.TRANSLUCENT;
		int firstRenderer = renderingTranslucentLayer ? this.chunkDirectionRenderers.size() - 1 : 0;
		int lastRenderer = renderingTranslucentLayer ? -1 : this.chunkDirectionRenderers.size();
		int int_5 = renderingTranslucentLayer ? -1 : 1;

		for (int i = firstRenderer; i != lastRenderer; i += int_5)
		{
			ChunkRenderer renderer = this.chunkDirectionRenderers.get(i).renderer;
			if (!renderer.getChunkRenderData().method_3641(blockRenderLayer_1))
			{
				++int_2;
				this.chunkRendererList.add(renderer, blockRenderLayer_1);
			}
		}

		this.client.getProfiler().swap(() ->
		{
			return "render_" + blockRenderLayer_1;
		});
		renderLayer(blockRenderLayer_1);
		client.getProfiler().pop();
		return int_2;
	}

	private void renderLayer(BlockRenderLayer blockRenderLayer_1)
	{
		this.client.gameRenderer.enableLightmap();
		if (GLX.useVbo())
		{
			GlStateManager.enableClientState(32884);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
			GlStateManager.enableClientState(32888);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
			GlStateManager.enableClientState(32888);
			GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
			GlStateManager.enableClientState(32886);
		}

		this.chunkRendererList.render(blockRenderLayer_1);
		if (GLX.useVbo())
		{
			List<VertexFormatElement> vertexFormatElements = VertexFormats.POSITION_COLOR_UV_LMAP.getElements();
			for (VertexFormatElement vertexFormatElement : vertexFormatElements)
			{
				VertexFormatElement.Type vertexFormatElement$Type_1 = vertexFormatElement.getType();
				int int_1 = vertexFormatElement.getIndex();
				switch (vertexFormatElement$Type_1)
				{
					case POSITION:
						GlStateManager.disableClientState(32884);
						break;
					case UV:
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + int_1);
						GlStateManager.disableClientState(32888);
						GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
						break;
					case COLOR:
						GlStateManager.disableClientState(32886);
						GlStateManager.clearCurrentColor();
				}
			}
		}

		this.client.gameRenderer.disableLightmap();
	}

	class ChunkRendererDirectionSet
	{
		private final ChunkRenderer renderer;
		private final Direction direction;
		private byte field_4126;
		private final int field_4122;

		private ChunkRendererDirectionSet(ChunkRenderer renderer, Direction direction, int int_1)
		{
			this.renderer = renderer;
			this.direction = direction;
			this.field_4122 = int_1;
		}

		public void method_3299(byte byte_1, Direction direction)
		{
			this.field_4126 = (byte) (this.field_4126 | byte_1 | 1 << direction.ordinal());
		}

		public boolean method_3298(Direction direction_1)
		{
			return (this.field_4126 & 1 << direction_1.ordinal()) > 0;
		}
	}
}
