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
  demoCutscene.setPath(new Path().withPoint(0, 0, 0).withPoint(30, 20, 5));
}
```
Check Cutscene and ClientCutscene for comments on all of the properties.