package com.raphydaphy.cutsceneapi.cutscene;

import net.minecraft.client.util.math.Vector3f;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.function.Function;

public class Path
{
	private Vector<Vector3f> points;

	private Vector<Cubic> xCubics;
	private Vector<Cubic> yCubics;
	private Vector<Cubic> zCubics;

	public Path()
	{
		this.points = new Vector<>();

		this.xCubics = new Vector<>();
		this.yCubics = new Vector<>();
		this.zCubics = new Vector<>();
	}

	public Path withPoint(Vector3f point)
	{
		this.points.add(point);
		return this;
	}

	public Path withPoint(float x, float y, float z)
	{
		return this.withPoint(new Vector3f(x, y, z));
	}

	public Vector<Vector3f> getPoints()
	{
		return points;
	}

	public Path build()
	{
		if (this.points.size() > 1)
		{
			calcNaturalCubic(points, Vector3f::x, xCubics);
			calcNaturalCubic(points, Vector3f::y, yCubics);
			calcNaturalCubic(points, Vector3f::z, zCubics);
		}
		return this;
	}

	public Vector3f getPoint(float position)
	{
		if (this.points.size() > 1)
		{
			position = position * xCubics.size();
			int cubicNum = (int) position;
			float cubicPos = (position - cubicNum);

			if (cubicNum < xCubics.size())
			{

				return new Vector3f(xCubics.get(cubicNum).eval(cubicPos),
						yCubics.get(cubicNum).eval(cubicPos),
						zCubics.get(cubicNum).eval(cubicPos));
			}
			return new Vector3f(this.points.get(this.points.size() - 1));
		}
		return new Vector3f(this.points.get(0));
	}

	private void calcNaturalCubic(List<Vector3f> valueCollection, Function<Vector3f, Float> getVal, Collection<Cubic> cubicCollection)
	{
		int num = valueCollection.size() - 1;

		float[] gamma = new float[num + 1];
		float[] delta = new float[num + 1];
		float[] D = new float[num + 1];

		int i;
		gamma[0] = 1.0f / 2.0f;
		for (i = 1; i < num; i++)
		{
			gamma[i] = 1.0f / (4.0f - gamma[i - 1]);
		}
		gamma[num] = 1.0f / (2.0f - gamma[num - 1]);

		Float p0 = getVal.apply(valueCollection.get(0));
		Float p1 = getVal.apply(valueCollection.get(1));

		delta[0] = 3.0f * (p1 - p0) * gamma[0];
		for (i = 1; i < num; i++)
		{
			p0 = getVal.apply(valueCollection.get(i - 1));
			p1 = getVal.apply(valueCollection.get(i + 1));
			delta[i] = (3.0f * (p1 - p0) - delta[i - 1]) * gamma[i];
		}
		p0 = getVal.apply(valueCollection.get(num - 1));
		p1 = getVal.apply(valueCollection.get(num));

		delta[num] = (3.0f * (p1 - p0) - delta[num - 1]) * gamma[num];

		D[num] = delta[num];
		for (i = num - 1; i >= 0; i--)
		{
			D[i] = delta[i] - gamma[i] * D[i + 1];
		}

		cubicCollection.clear();

		for (i = 0; i < num; i++)
		{
			p0 = getVal.apply(valueCollection.get(i));
			p1 = getVal.apply(valueCollection.get(i + 1));

			cubicCollection.add(new Cubic(p0, D[i], 3 * (p1 - p0) - 2 * D[i] - D[i + 1], 2 * (p0 - p1) + D[i] + D[i + 1]));
		}
	}

	public static class Cubic
	{
		private float a, b, c, d;

		public Cubic(float a, float b, float c, float d)
		{
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		public float eval(float u)
		{
			return (((d * u) + c) * u + b) * u + a;
		}
	}
}