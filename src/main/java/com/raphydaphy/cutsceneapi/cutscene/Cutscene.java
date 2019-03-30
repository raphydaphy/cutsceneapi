package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.fakeworld.CutsceneChunk;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public interface Cutscene
{
	/**
	 * Called once per tick
	 */
	@Environment(EnvType.CLIENT)
	void tick();

	/**
	 * Called once per frame
	 */
	@Environment(EnvType.CLIENT)
	void render();

	/**
	 * Called once per frame after the players look direction is set
	 * Can be used to adjust this direction
	 */
	@Environment(EnvType.CLIENT)
	void updateLook();

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
	 * @param initCallback A function to be called before the start of the cutscene
	 */
	@Environment(EnvType.CLIENT)
	void setInitCallback(Consumer<Cutscene> initCallback);

	/**
	 * @param chunkGenCallback A function which will be called whenever a fake world chunk is generated for the cutscene
	 */
	@Environment(EnvType.CLIENT)
	void setChunkGenCallback(Consumer<CutsceneChunk> chunkGenCallback);

	/**
	 * @param tickCallback A function to be called once per tick while the cutscene is playing
	 */
	@Environment(EnvType.CLIENT)
	void setTickCallback(Consumer<Cutscene> tickCallback);

	/**
	 * @param renderCallback A function which will be called once per frame when rendering
	 */
	@Environment(EnvType.CLIENT)
	void setRenderCallback(Consumer<Cutscene> renderCallback);

	/**
	 * @param finishCallback A function which will be called at the end of the cutscene
	 */
	@Environment(EnvType.CLIENT)
	void setFinishCallback(Consumer<Cutscene> finishCallback);

	/**
	 * @param worldType The type of world which should be used during the cutscene
	 */
	@Environment(EnvType.CLIENT)
	void setWorldType(CutsceneWorldType worldType);

	/**
	 * @param nextCutscene The cutscene which should play after this one
	 */
	@Environment(EnvType.CLIENT)
	void setNextCutscene(Cutscene nextCutscene);

	/**
	 * @return A copy of the cutscene
	 */
	@Environment(EnvType.CLIENT)
	Cutscene copy();

	/**
	 * @return The fake world used by the cutscene, if any
	 */
	@Environment(EnvType.CLIENT)
	CutsceneWorld getWorld();

	/**
	 * @return The cutscene which should play after this one
	 */
	@Environment(EnvType.CLIENT)
	Cutscene getNextCutscene();

	/**
	 * @return The function which should be run whenever a cutscene chunk is generated
	 */
	@Environment(EnvType.CLIENT)
	Consumer<CutsceneChunk> getChunkGenCallback();

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

	/**
	 * @return True if the ingame HUD should be hidden at the current point during the transition
	 */
	@Environment(EnvType.CLIENT)
	boolean shouldHideHud();
}
