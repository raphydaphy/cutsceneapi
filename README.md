# CutScene API
Tools to help developers add CutScenes to their mods.


## Getting Started
Create a Custcene instance in your common mod initializer, setting the length to however long you want
```java
public static Cutscene DEMO_CUTSCENE = new DefaultCutscene(500)
```
Add a constructor to your client mod initializer. Replace the cutscene with a ClientCutscene  instance of the same length
```java
public class DemoModClient implements ClientModInitializer
{
  public DemoModClient()
  {
    DemoMod.DEMO_CUTSCENE = new DefaultClientCutscene(DemoMod.DEMO_CUTSCENE.getLength());
  }
}
```
Register the cutscene in your common mod initializer's `onInitialize` method
```java
public void onInitialize()
{
  CutsceneRegistry.register(new Identifier("demomod", "demo_cutscene"), DEMO_CUTSCENE);
}
```

Add any clientside cutscene properties to the cutscene in your client mod initializers `onInitializeClient` method
```java
public void onInitializeClient()
{
  ClientCutscene demoCutscene = (ClientCutscene)DemoMod.DEMO_CUTSCENE;

  // Shaders are stored in json files using the same system as vanilla
  demoCutscene.setShader(new Identifier("demo_mod", "shaders/epic_4k_shader.json");

  // You need a path for your cutscene camera to follow. Create one and add points for the camera to move through
  demoCutscene.setPath(new SplinePath().withPoint(0, 0, 0).withPoint(30, 20, 5));
}
```
Check Cutscene and ClientCutscene for comments on all of the properties.

## Recording Camera Paths
If you want more control over the camera movement during your cutscene, you can record movement ingame and save it as a file. To begin recording, navigate to a suitable starting position ingame and run the command.
```mcfunction
cutscene record camera @p
```
Once the recording has started, move around in the world and your movements will be recorded. Once you have finished, stop the recording.
```mcfunction
cutscene record stop @p
```
This will place a `.cpath` file in your Minecraft folder, in the `cutscenes/paths` directory. Move this file into your mod assets and give it a suitable name. You can use it in your cutscene by setting the path during the init callback on the client-side.
```java
cutscene.setInitCallback((cutscene) -> {
  ((ClientCutscene)cutscene).setCameraPath(RecordedPath.fromFile(new Identifier(CutsceneAPI.DOMAIN, "recorded.cpath")));
});
```

## Using Fake Worlds
Sometimes you might want to show a world in your cutscene which is different to the real one. Luckily CutsceneAPI provides several tools for this! Your cutscene can have one of many different types of worlds. You can set the type with `ClientCutscene#setWorldType(CutsceneWorldType type)`. By default, cutscenes use the real world.

It is easy to implement a void or clone cutscene world - simply set the world type to `CutsceneWorldType.VOID` or `CutsceneWorldType.CLONE`. A void world will be empty, but you can place blocks in it before or during the cutscene, while a clone world is pre-filled with all of the blocks from the real world. The `PREVIOUS` and `CUSTOM` world types are a bit more complex. `PREVIOUS` uses whatever world was bound at the start of the cutscene, while `CUSTOM` gives you full control over the world.

### Generating a World
You can generate a world for use in a cutscene. This is not the recommended as it takes a significant amount of time to generate, and uses a lot of memory if you keep it loaded instead of destroying it after it has been created. However, CutsceneAPI provides an easy way of generating a CutsceneWorld if you still want to use this method. You can use the chunk-gen callback to modify each chunk directly after their creation if you need to make changes.
```java
cutscene.setWorldInitCallback((cutscene) -> {
  ((ClientCutscene)cutscene).setWorld(CutsceneWorld.createCached(seed, generationRadius, generateStructures, (chunk) -> {}));
})
```
It is best to cache these worlds when the game is loaded instead of generating them at the beginning of the cutscene, but this will consume additional memory and is part of why this approach is not recommended.

### Loading a Serialized World
The recommended way to load a custom fake world is to serialize the chunks you want to use, and load them from a file at the start of the cutscene. To serialize chunks, travel to `0, ~, 0` in a world and run the command.
```mcfunction
cutscene world serialize @p
```
This will serialize a 30x30 chunk area and save it in `.minecraft/cutscenes/worlds/serialized.cworld`. You should rename this file and move it to your mod assets directory. In order to make the region accessible ingame, use the method provided by the API to copy in your client mod initializer. Using the sprite registry callback for this is silly but it is an easy way to ensure that your region is copied at the correct time during the loading process.
```java
@Override
public void onInitializeClient()
{
  ClientSpriteRegistryCallback.registerBlockAtlas((atlasTexture, registry) ->
  {
    CutsceneWorldLoader.copyCutsceneWorld(new Identifier("modid", "serialized.cworld"), "serialized.cworld");
  });
}
```
Once your region has been copied, you can easily use it in any cutscene which uses the `CUSTOM` world type.
```java
cachedWorld.setWorldInitCallback((cutscene) -> {
  MinecraftClient client = MinecraftClient.getInstance();
  CutsceneWorld cutsceneWorld = new CutsceneWorld(client, client.world, null, false);
  CutsceneWorldLoader.addChunks("serialized.cworld", cutsceneWorld, 15);
  cutscene.setWorld(cutsceneWorld);
});
```