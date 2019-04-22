package com.raphydaphy.cutsceneapi.command;

import com.google.gson.JsonObject;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.util.PacketByteBuf;

public class CutsceneArgumentSerializer implements ArgumentSerializer<CutsceneArgumentType> {

    @Override
    public void toPacket(CutsceneArgumentType cutsceneArgumentType, PacketByteBuf packetByteBuf) {
    }

    @Override
    public CutsceneArgumentType fromPacket(PacketByteBuf packetByteBuf) {
        return new CutsceneArgumentType();
    }

    @Override
    public void toJson(CutsceneArgumentType cutsceneArgumentType, JsonObject jsonObject) {

    }
}
