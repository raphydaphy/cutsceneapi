package com.raphydaphy.cutsceneapi.api;

public interface Cutscene
{
	/**
	 * @param length The new length in ticks
	 */
	void setLength(int length);

	/**
	 * @return The cutscene length in ticks
	 */
	int getLength();

	/**
	 * @return A copy of the cutscene
	 */
	Cutscene copy();
}
