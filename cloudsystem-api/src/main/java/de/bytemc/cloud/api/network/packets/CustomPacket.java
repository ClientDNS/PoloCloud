package de.bytemc.cloud.api.network.packets;

import de.bytemc.cloud.api.json.Document;
import de.bytemc.network.packets.IPacket;
import de.bytemc.network.packets.NetworkByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

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
