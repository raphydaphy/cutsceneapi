package com.raphydaphy.cutsceneapi.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public interface ClientCutscene extends Cutscene
{
	/**
	 * @param path The path which the camera should follow during the cutscene
	 */
	@SideOnly(Side.CLIENT)
	void setClientPath(Path path);

	/**
	 * @param callback A function which is called at the start of the cutscene
	 */
	void setClientInitCallback(Consumer<ClientCutscene> callback);

	/**
	 * @param callback A function which will ce called every tick during the cutscene
	 */
	void setClientTickCallback(Consumer<ClientCutscene> callback);

	/**
	 * @param callback A function which will be called once per frame during the cutscene
	 */
	@SideOnly(Side.CLIENT)
	void setRenderCallback(Consumer<ClientCutscene> callback);

	/**
	 * @param callback A function which will be called when the cutscene finishes
	 */
	void setClientEndCallback(Consumer<ClientCutscene> callback);

	/**
	 * Called once per frame while the cutscene is playing
	 */
	void render();

	/**
	 * Called once per tick on the client
	 */
	void clientTick();
}
