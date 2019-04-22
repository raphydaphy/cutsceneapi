package com.raphydaphy.cutsceneapi.cutscene;

import com.mojang.blaze3d.platform.GLX;
import com.raphydaphy.cutsceneapi.api.ClientCutscene;
import com.raphydaphy.cutsceneapi.api.Cutscene;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneChunk;
import com.raphydaphy.cutsceneapi.fakeworld.CutsceneWorld;
import com.raphydaphy.cutsceneapi.mixin.client.ClientPlayNetworkHandlerHooks;
import com.raphydaphy.cutsceneapi.mixin.client.GameRendererHooks;
import com.raphydaphy.cutsceneapi.mixin.client.MinecraftClientHooks;
import com.raphydaphy.cutsceneapi.path.Path;
import com.raphydaphy.cutsceneapi.utils.CutsceneUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class DefaultClientCutscene extends DefaultCutscene implements ClientCutscene {
    // Settings
    private Transition introTransition;
    private Transition outroTransition;
    private Identifier shader;
    private Consumer<ClientCutscene> worldInitCallback;
    private Consumer<CutsceneChunk> chunkGenCallback;
    private Consumer<ClientCutscene> renderCallback;
    private Path path;
    private CutsceneWorldType worldType = CutsceneWorldType.REAL;
    private ClientCutscene nextCutscene;
    private boolean renderBars;

    // Client Data
    private CutsceneCameraEntity camera;
    private CutsceneWorld cutsceneWorld;
    private int startPerspective;
    private float startPitch;
    private float startYaw;
    private boolean usingShader = false;
    private boolean setCamera = false;
    private int timeSinceWorldSet = 0;

    public DefaultClientCutscene(int length) {
        super(length);
    }

    private void start() {
        MinecraftClient client = MinecraftClient.getInstance();
        this.startPerspective = client.options.perspective;
        this.startPitch = client.player.pitch;
        this.startYaw = client.player.yaw;
        if (!worldType.isRealWorld() && worldType != CutsceneWorldType.PREVIOUS && worldType != CutsceneWorldType.CUSTOM) {
            this.cutsceneWorld = new CutsceneWorld(client, client.world, chunkGenCallback, this.worldType == CutsceneWorldType.CLONE);
        }
        if (this.initCallback != null) this.initCallback.accept(this);
        if (introTransition != null) introTransition.init();
        if (outroTransition != null) outroTransition.init();
        this.camera = new CutsceneCameraEntity(client.world).withPos(this.path.getPoint(0));
        this.started = true;
        this.setCamera = false;
    }

    @Override
    public void tick() {
        if (!ended) {
            MinecraftClient client = MinecraftClient.getInstance();

            if (ticks == 0) {
                start();
            }

            if (shouldHideHud()) {
                // Move Camera
                camera.update();
                camera.moveTo(path.getPoint(ticks / (float) length));

                // Set Camera
                if (!setCamera) {
                    client.cameraEntity = camera;

                    enableShader();
                    setCamera = true;
                }

                if (!this.worldType.isRealWorld() && this.worldType != CutsceneWorldType.PREVIOUS) {
                    if (!(client.world instanceof CutsceneWorld)) {
                        if (worldInitCallback != null) worldInitCallback.accept(this);
                        client.player.setWorld(cutsceneWorld);
                        client.world = cutsceneWorld;
                        ((MinecraftClientHooks) client).setCutsceneWorld(cutsceneWorld);
                        ClientPlayNetworkHandler handler = client.getNetworkHandler();
                        if (handler != null) {
                            ((ClientPlayNetworkHandlerHooks) handler).setCutsceneWorld(cutsceneWorld);
                        }
                        this.cutsceneWorld.addPlayer(client.player);
                        timeSinceWorldSet = 0;
                    } else if (timeSinceWorldSet == 1) {
                        // Why? It didn't work when I reloaded earlier :D
                        client.worldRenderer.reload();
                    }

                    timeSinceWorldSet++;
                }
                // Fix perspective
                if (client.options.perspective != 0) {
                    client.options.perspective = 0;
                    client.worldRenderer.scheduleTerrainUpdate();
                    enableShader();
                }

                // Set Camera Look
                float percent = ticks / (float) length;

                Pair<Float, Float> rotation = path.getRotation(percent);
                camera.prevYaw = camera.yaw;
                camera.prevPitch = camera.pitch;
                camera.pitch = rotation.getLeft();
                camera.yaw = rotation.getRight();
            } else {
                // Restore real world
                if (!worldType.isRealWorld()) CutsceneManager.stopFakeWorld();

                // Disable Shader
                if (usingShader) {
                    client.gameRenderer.disableShader();
                    usingShader = false;
                }

                // Restore player camera
                disableCamera();

                // Restore perspective
                if (client.options.perspective != startPerspective) {
                    client.options.perspective = startPerspective;
                    client.worldRenderer.scheduleTerrainUpdate();
                }
            }

            // Update Transitions
            if (introTransition != null && ticks < introTransition.length) introTransition.update();
            else if (outroTransition != null && ticks > length - outroTransition.length) outroTransition.update();

            // Callback
            if (tickCallback != null) tickCallback.accept(this);

            ticks++;

            if (ticks == length) {
                end();
            }
        }
    }

    private void enableShader() {
        if (this.shader != null && !usingShader) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.worldRenderer.scheduleTerrainUpdate();
            if (GLX.usePostProcess) {
                ((GameRendererHooks) client.gameRenderer).useShader(this.shader);
            }
            usingShader = true;
        }
    }

    private void disableCamera() {
        if (setCamera) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.setCameraEntity(client.player);
            client.worldRenderer.scheduleTerrainUpdate();
            setCamera = false;
        }
    }

    @Override
    public void render() {
        if (started && !ended) {
            MinecraftClient client = MinecraftClient.getInstance();

            // Render Black Bars
            if (shouldHideHud() && renderBars) {
                int screenWidth = client.window.getScaledWidth();
                int screenHeight = client.window.getScaledHeight();

                int desiredHeight = screenWidth / 16 * 9;
                int minBarHeight = screenHeight / 16;
                int barHeight = (screenHeight - desiredHeight) / 2;
                if (barHeight < minBarHeight) barHeight = minBarHeight;
                CutsceneUtils.drawRect(0, 0, screenWidth, barHeight, 1, 0, 0, 0);
                CutsceneUtils.drawRect(0, screenHeight - barHeight, screenWidth, screenHeight, 1, 0, 0, 0);
            }

            // Render Transitions
            if (introTransition != null && ticks < introTransition.length) {
                introTransition.render(client, client.getTickDelta());
            } else if (outroTransition != null && ticks > length - outroTransition.length) {
                outroTransition.render(client, client.getTickDelta());
            }

            // Callback
            if (renderCallback != null) renderCallback.accept(this);
        }
    }

    @Override
    public void updateLook() {
        if (started && !ended) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.player.pitch = startPitch;
            client.player.yaw = startYaw;
        }
    }

    @Override
    public void end() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (nextCutscene == null || nextCutscene.getWorldType() != CutsceneWorldType.PREVIOUS) {
            disableCamera();
            // Restore real world
            if (!worldType.isRealWorld()) CutsceneManager.stopFakeWorld();

            // Disable Shader
            if (usingShader) {
                client.gameRenderer.disableShader();
                usingShader = false;
            }

            // Restore perspective
            if (client.options.perspective != startPerspective) {
                client.options.perspective = startPerspective;
                client.worldRenderer.scheduleTerrainUpdate();
            }

            if (finishCallback != null) finishCallback.accept(this);

            if (nextCutscene != null) {
                CutsceneManager.startClient(nextCutscene.getID());
            } else {
                CutsceneManager.finishClient();
            }
        } else {
            CutsceneManager.startClient(nextCutscene.getID());
            ClientCutscene newCutscene = (ClientCutscene) CutsceneManager.getCurrentCutscene();
            newCutscene.setWorld(this.cutsceneWorld);
            newCutscene.setChunkGenCallback(this.chunkGenCallback);
        }
        ended = true;
    }

    @Override
    public void setShader(Identifier shader) {
        this.shader = shader;
    }

    @Override
    public void setIntroTransition(Transition introTransition) {
        this.introTransition = introTransition;
    }

    @Override
    public void setOutroTransition(Transition outroTransition) {
        this.outroTransition = outroTransition;
    }

    @Override
    public void setInitCallback(Consumer<Cutscene> initCallback) {
        this.initCallback = initCallback;
    }

    @Override
    public void setTickCallback(Consumer<Cutscene> tickCallback) {
        this.tickCallback = tickCallback;
    }

    @Override
    public void setRenderCallback(Consumer<ClientCutscene> renderCallback) {
        this.renderCallback = renderCallback;
    }

    @Override
    public void setFinishCallback(Consumer<Cutscene> finishCallback) {
        this.finishCallback = finishCallback;
    }

    @Override
    public void enableBlackBars() {
        this.renderBars = true;
    }

    @Override
    public Cutscene copy() {
        DefaultClientCutscene cutscene = new DefaultClientCutscene(length);

        cutscene.setID(getID());
        cutscene.introTransition = this.introTransition;
        cutscene.outroTransition = this.outroTransition;
        cutscene.shader = this.shader;
        cutscene.initCallback = this.initCallback;
        cutscene.worldInitCallback = this.worldInitCallback;
        cutscene.chunkGenCallback = this.chunkGenCallback;
        cutscene.tickCallback = this.tickCallback;
        cutscene.renderCallback = this.renderCallback;
        cutscene.finishCallback = this.finishCallback;
        cutscene.path = this.path;
        cutscene.worldType = this.worldType;
        cutscene.nextCutscene = this.nextCutscene;
        cutscene.renderBars = this.renderBars;

        return cutscene;
    }

    @Override
    public CutsceneWorld getWorld() {
        return cutsceneWorld;
    }

    @Override
    public void setWorld(CutsceneWorld world) {
        this.cutsceneWorld = world;
    }

    @Override
    public CutsceneWorldType getWorldType() {
        return worldType;
    }

    @Override
    public void setWorldType(CutsceneWorldType worldType) {
        this.worldType = worldType;
    }

    @Override
    public ClientCutscene getNextCutscene() {
        return nextCutscene;
    }

    @Override
    public void setNextCutscene(ClientCutscene nextCutscene) {
        this.nextCutscene = nextCutscene;
    }

    @Override
    public void setWorldInitCallback(Consumer<ClientCutscene> worldInitCallback) {
        this.worldInitCallback = worldInitCallback;
    }

    @Override
    public void setChunkGenCallback(Consumer<CutsceneChunk> chunkGenCallback) {
        this.chunkGenCallback = chunkGenCallback;
    }

    @Override
    public int getTicks() {
        return ticks;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public Path getCameraPath() {
        return path;
    }

    @Override
    public void setCameraPath(Path path) {
        this.path = path;
    }

    @Override
    public boolean shouldHideHud() {
        if (introTransition != null && ticks < introTransition.length && introTransition.isFirstHalf()) return false;
        else if (outroTransition != null && ticks > length - outroTransition.length && !outroTransition.isFirstHalf())
            return false;
        else if (ticks >= length || ended) return false;
        return true;
    }
}
