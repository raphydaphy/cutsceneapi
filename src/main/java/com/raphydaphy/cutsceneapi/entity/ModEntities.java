package com.raphydaphy.cutsceneapi.entity;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {
  public static EntityType CUTSCENE_CAMERA_ENTITY;

  public static void register() {
    CUTSCENE_CAMERA_ENTITY = Registry.register(
      Registry.ENTITY_TYPE,
      new Identifier(CutsceneAPI.MODID, "cutscene_camera"),
      FabricEntityTypeBuilder.create(
        SpawnGroup.MISC,
        (t, w) -> new CutsceneCameraEntity(w)
      ).dimensions(new EntityDimensions(1, 1, true)).build()
    );
  }

  public static void registerRenderers() {
    /*
    EntityRendererRegistry.INSTANCE.register(CUTSCENE_CAMERA_ENTITY, (dispatcher, context) -> {
      return null;
    });
    */
  }
}
