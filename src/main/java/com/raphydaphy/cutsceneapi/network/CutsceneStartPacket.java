package com.raphydaphy.cutsceneapi.network;

import com.raphydaphy.crochet.network.IPacket;
import com.raphydaphy.crochet.network.MessageHandler;
import com.raphydaphy.cutsceneapi.CutsceneAPI;
import com.raphydaphy.cutsceneapi.cutscene.CutsceneManager;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class CutsceneStartPacket implements IPacket {
    public static final Identifier ID = new Identifier(CutsceneAPI.DOMAIN, "cutscene_start");

    private Identifier cutscene;

    private CutsceneStartPacket() {

    }

    public CutsceneStartPacket(Identifier cutscene) {
        this.cutscene = cutscene;
    }

    @Override
    public void read(PacketByteBuf buf) {
        this.cutscene = new Identifier(buf.readString(buf.readInt()));
    }

    @Override
    public void write(PacketByteBuf buf) {
        String id = this.cutscene.toString();
        buf.writeInt(id.length());
        buf.writeString(id);
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    public static class Handler extends MessageHandler<CutsceneStartPacket> {
        @Override
        protected CutsceneStartPacket create() {
            return new CutsceneStartPacket();
        }

        @Override
        public void handle(PacketContext ctx, CutsceneStartPacket message) {
            CutsceneManager.startClient(message.cutscene);
        }
    }
}
