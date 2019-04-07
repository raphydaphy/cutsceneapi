package com.raphydaphy.cutsceneapi.path;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.utils.CutsceneUtils;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class RecordedPath implements Path
{
	// Length in ticks
	private final int length;

	// One per tick
	private final Vector3f[] positions;
	private final float[] pitch;
	private final float[] yaw;

	public RecordedPath(Vector3f[] positions, float[] pitch, float[] yaw)
	{
		int totalPositions = positions.length;
		if (totalPositions != pitch.length || totalPositions != yaw.length)
		{
			CutsceneAPI.getLogger().error("Tried to create a RecordedPath with different length position/pitch/yaw arrays!");
		}
		this.length = totalPositions;
		this.positions = positions;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public static RecordedPath.Builder builder()
	{
		return new RecordedPath.Builder();
	}

	@Override
	public Vector3f getPoint(float position)
	{
		float index = position * length;
		int cur = (int)index;
		float time = index - cur;

		Vector3f curPos = positions[cur];
		Vector3f prevPos = positions[cur <= 0 ? cur : cur - 1];

		return new Vector3f(CutsceneUtils.lerp(prevPos.x(), curPos.x(), time), CutsceneUtils.lerp(prevPos.y(), curPos.y(), time), CutsceneUtils.lerp(prevPos.z(), curPos.z(), time));
	}

	@Override
	public Pair<Float, Float> getRotation(float position)
	{
		float index = position * length;
		int cur = (int)index;
		int prev = cur <= 0 ? cur : cur - 1;
		float time = index - cur;

		return new Pair<>(CutsceneUtils.lerp(pitch[prev], pitch[cur], time), CutsceneUtils.lerp(yaw[prev], yaw[cur], time));
	}

	public static class Builder
	{
		// One position/pitch/yaw per tick
		private final List<Vector3f> positions;
		private final List<Float> pitch;
		private final List<Float> yaw;

		private Builder()
		{
			this.positions = new ArrayList<>();
			this.pitch = new ArrayList<>();
			this.yaw = new ArrayList<>();
		}

		public Builder with(Vector3f point, float pitch, float yaw)
		{
			this.positions.add(point);
			this.pitch.add(pitch);
			this.yaw.add(yaw);
			return this;
		}

		public RecordedPath build()
		{
			Vector3f[] positions = new Vector3f[this.positions.size()];
			float[] pitch = new float[this.pitch.size()];
			float[] yaw = new float[this.yaw.size()];
			for (int i = 0; i < positions.length; i++)
			{
				positions[i] = this.positions.get(i);
				pitch[i] = this.pitch.get(i);
				yaw[i] = this.yaw.get(i);
			}
			return new RecordedPath(positions, pitch, yaw);
		}
	}
}
