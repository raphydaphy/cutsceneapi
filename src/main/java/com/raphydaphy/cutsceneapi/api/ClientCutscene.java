package com.raphydaphy.cutsceneapi.api;

import com.raphydaphy.cutsceneapi.cutscene.CutsceneWorldType;
import com.raphydaphy.cutsceneapi.cutscene.Path;
import com.raphydaphy.cutsceneapi.cutscene.Transition;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneChunk;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface ClientCutscene extends Cutscene
{

	/**
	 * Called once per frame
	 */
	void render();

	/**
	 * Called once per frame after the players look direction is set
	 * Can be used to adjust this direction
	 */
	void updateLook();

	/**
	 * @param path A new path for the camera to travel on
	 */
	void setCameraPath(Path path);

	/**
	 * @param shader The path to the shader which the cutscene should use
	 */
	void setShader(Identifier shader);

	/**
	 * @param introTransition The transition which should be played at the start of the cutscene
	 */
	void setIntroTransition(Transition introTransition);

	/**
	 * @param outroTransition The transition which should be played at the end of the cutscene
	 */
	void setOutroTransition(Transition outroTransition);

	/**
	 * @param chunkGenCallback A function which will be called whenever a fake world chunk is generated for the cutscene
	 */
	void setChunkGenCallback(Consumer<CutsceneChunk> chunkGenCallback);

	/**
	 * @param renderCallback A function which will be called once per frame when rendering
	 */
	void setRenderCallback(Consumer<ClientCutscene> renderCallback);

	/**
	 * @param worldType The type of world which should be used during the cutscene
	 */
	void setWorldType(CutsceneWorldType worldType);

	/**
	 * @param nextCutscene The cutscene which should play after this one
	 */
	void setNextCutscene(ClientCutscene nextCutscene);

	/**
	 * @return The fake world used by the cutscene, if any
	 */
	CutsceneWorld getWorld();

	/**
	 * @return The cutscene which should play after this one
	 */
	ClientCutscene getNextCutscene();

	/**
	 * @return The function which should be run whenever a cutscene chunk is generated
	 */
	Consumer<CutsceneChunk> getChunkGenCallback();

	/**
	 * @return The current path which the camera is traveling on
	 */
	Path getCameraPath();

	/**
	 * @return True if the ingame HUD should be hidden at the current point during the transition
	 */
	boolean shouldHideHud();
}
