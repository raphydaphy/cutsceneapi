package com.raphydaphy.cutsceneapi.api;

import org.lwjgl.util.vector.Vector3f;

import java.util.Vector;

public interface Path
{
	Path withPoint(Vector3f point);

	Path withPoint(float x, float y, float z);

	Path build();

	Vector<Vector3f> getPoints();

	Vector3f getPoint(float position);
}
