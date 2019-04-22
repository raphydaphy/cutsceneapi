package com.raphydaphy.cutsceneapi.cutscene;

import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.api.Cutscene;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CutsceneRegistry {
    private static Map<Identifier, Cutscene> REGISTRY = new HashMap<>();

    public static void register(Identifier id, Cutscene cutscene) {
        if (!REGISTRY.containsKey(id)) {
            cutscene.setID(id);
            REGISTRY.put(id, cutscene);
            CutsceneAPI.log(Level.DEBUG, "Registering cutscene " + id);
            return;
        }
        CutsceneAPI.log(Level.ERROR, "Tried to replace existing cutscene with ID " + id + "! Use CutsceneRegistry#replace if you wish to replace a cutscene.");
    }

    public static Cutscene get(Identifier id) {
        return REGISTRY.get(id).copy();
    }

    public static Set<Identifier> getIDs() {
        return REGISTRY.keySet();
    }
}
