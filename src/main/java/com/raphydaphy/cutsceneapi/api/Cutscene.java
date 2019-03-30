package com.raphydaphy.cutsceneapi.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Consumer;

public interface Cutscene
{
	/**
	 * @param id The ID which should be used to register the cutscene
	 */
	void setID(ResourceLocation id);

	/**
	 * @param length The new length in ticks
	 */
	void setLength(int length);

	/**
	 * @param path The path which the camera should follow during the cutscene
	 */
	@SideOnly(Side.CLIENT)
	void setPath(Path path);

	/**
	 * @param callback A function which is called at the start of the cutscene
	 */
	void setInitCallback(Consumer<Cutscene> callback);

	/**
	 * @param callback A function which will ce called every tick during the cutscene
	 */
	void setTickCallback(Consumer<Cutscene> callback);

	/**
	 * @param callback A function which will be called once per frame during the cutscene
	 */
	@SideOnly(Side.CLIENT)
	void setRenderCallback(Consumer<Cutscene> callback);

	/**
	 * @param callback A function which will be called when the cutscene finishes
	 */
	void setEndCallback(Consumer<Cutscene> callback);

	/**
	 * Called once per tick while the cutscene is playing
	 */
	void tick();

	/**
	 * Called once per frame while the cutscene is playing
	 */
	@SideOnly(Side.CLIENT)
	void render();

	/**
	 * @return The registry ID of the cutscene
	 */
	ResourceLocation getID();

	/**
	 * @return The cutscene length in ticks
	 */
	int getLength();

	/**
	 * @return A new cutscene with the same settings
	 */
	Cutscene copy(boolean client);
}
