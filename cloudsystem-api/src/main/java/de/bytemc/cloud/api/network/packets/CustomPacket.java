package de.bytemc.cloud.api.network.packets;

import de.bytemc.cloud.api.json.Document;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import org.jetbrains.annotations.NotNull;

public final class CustomPacket implements IPacket {

    private Document document;

    public CustomPacket() {}

    public CustomPacket(final @NotNull Document document) {
        this.document = document;
    }

    @Override
    public void write(NetworkByteBuf networkByteBuf) {
        networkByteBuf.writeString(this.document.getJsonObject().toString());
    }

    @Override
    public void read(NetworkByteBuf networkByteBuf) {
        this.document = new Document(networkByteBuf.readString());
    }

    public Document getDocument() {
        return this.document;
    }

}
