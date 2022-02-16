package de.polocloud.api.network.packet;

import de.polocloud.api.json.Document;
import de.polocloud.network.packet.IPacket;
import de.polocloud.network.packet.NetworkByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class CustomPacket implements IPacket {

    private Document document;

    @Override
    public void write(NetworkByteBuf networkByteBuf) {
        networkByteBuf.writeString(this.document.getJsonObject().toString());
    }

    @Override
    public void read(NetworkByteBuf networkByteBuf) {
        this.document = new Document(networkByteBuf.readString());
    }

}
