package de.bytemc.network.packets;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class NetworkByteBuf {

    private final ByteBuf byteBuf;

    public void writeString(String string) {
        var bytes = string.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
    public String readString() {
        var bytes = new byte[byteBuf.readInt()];
        byteBuf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public <T extends Enum<T>> T readEnum() {
        boolean nulled = this.byteBuf.readBoolean();
        if (nulled) return null;
        Class<?> enumClass;
        try {
            String classString = this.readString();
            enumClass = Class.forName(classString);
            int varInt = this.readVarInt();
            return (T) enumClass.getEnumConstants()[varInt];
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeEnum(Enum<?> val) {
        this.byteBuf.writeBoolean(val == null);
        if (val != null) {
            this.writeString(val.getDeclaringClass().getName());
            this.writeVarInt(val.ordinal());
        }
    }

    public void writeVarInt(int input) {
        while ((input & -128) != 0){
            byteBuf.writeByte(input & 127 | 128);
            input >>>= 7;
        }
        byteBuf.writeByte(input);
    }

    public int readInt() {
        return this.byteBuf.readInt();
    }

    public boolean readBoolean() {
        return this.byteBuf.readBoolean();
    }

    public void writeBoolean(boolean value) {
        this.byteBuf.writeBoolean(value);
    }

    public void writeInt(int value) {
        this.byteBuf.writeInt(value);
    }

    public int readVarInt() {
        int i = 0;
        int j = 0;

        while (true) {
            byte b0 = byteBuf.readByte();
            i |= (b0 & 127) << j++ * 7;

            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
            if ((b0 & 128) != 128) {
                break;
            }
        }
        return i;
    }

    public UUID readUUID() {
        return new UUID(byteBuf.readLong(),byteBuf.readLong());
    }

    public void writeUUID(UUID uniqueId) {
        byteBuf.writeLong(uniqueId.getMostSignificantBits());
        byteBuf.writeLong(uniqueId.getLeastSignificantBits());
    }
}
