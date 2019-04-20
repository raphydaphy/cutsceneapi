package com.raphydaphy.cutsceneapi.path;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Pair;

public interface Path {
    /**
     * Get the position at a given point along the path
     *
     * @param position How much of the path has been traveled? Between 0 and 1
     * @return The current position along the path
     */
    Vector3f getPoint(float position);

    /**
     * Get the pitch and yaw at a given point
     *
     * @param position How much of the path has been traveled? Between 0 and 1
     * @return A pair containing the pitch (left) and yaw (right) which the camera should have
     */
    Pair<Float, Float> getRotation(float position);
}
