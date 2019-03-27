package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import sun.tools.java.Identifier;

import java.util.function.Consumer;

public interface ICutscene
{
	/**
	 * @param path A new path for the camera to travel on
	 */
	@Environment(EnvType.CLIENT)
	void setCameraPath(Path path);

	/**
	 * @param shader The path to the shader which the cutscene should use
	 */
	@Environment(EnvType.CLIENT)
	void setShader(Identifier shader);

	/**
	 * @param introTransition The transition which should be played at the start of the cutscene
	 */
	@Environment(EnvType.CLIENT)
	void setIntroTransition(Transition introTransition);

	/**
	 * @param outroTransition The transition which should be played at the end of the cutscene
	 */
	@Environment(EnvType.CLIENT)
	void setOutroTransition(Transition outroTransition);

	/**
	 * @param introCallback A function to be called before the start of the cutscene
	 */
	@Environment(EnvType.CLIENT)
	void setIntroCallback(Consumer<ICutscene> introCallback);

	/**
	 * @param tickCallback A function to be called once per tick while the cutscene is playing
	 */
	@Environment(EnvType.CLIENT)
	void setTickCallback(Consumer<ICutscene> tickCallback);

	/**
	 * @param outroCallback A function which will be called at the end of the cutscene
	 */
	@Environment(EnvType.CLIENT)
	void setOutroCallback(Consumer<ICutscene> outroCallback);

	/**
	 * @param worldType The type of world which should be used during the cutscene
	 */
	@Environment(EnvType.CLIENT)
	void setWorldType(CutsceneWorldType worldType);

	/**
	 * @return The fake world used by the cutscene, if any
	 */
	@Environment(EnvType.CLIENT)
	CutsceneWorld getWorld();

	/**
	 * @return The amount of ticks since the cutscene started
	 */
	@Environment(EnvType.CLIENT)
	int getTicks();

	/**
	 * @return The cutscene's length in ticks
	 */
	int getLength();

	/**
	 * @return The current path which the camera is traveling on
	 */
	@Environment(EnvType.CLIENT)
	Path getCameraPath();
}
