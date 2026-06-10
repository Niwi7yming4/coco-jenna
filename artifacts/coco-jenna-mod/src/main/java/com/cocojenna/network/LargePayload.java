package com.cocojenna.network;

import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

/** UTF-8 byte payloads for large JSON / text (avoids writeUtf character limits). */
public final class LargePayload {

    public static final int MAX_BYTES = 65536;

    private LargePayload() {}

    public static void writeUtf8(FriendlyByteBuf buf, String text) {
        String safe = text == null ? "" : text;
        byte[] bytes = safe.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_BYTES) {
            throw new EncoderException("Payload too large (" + bytes.length + " > " + MAX_BYTES + ")");
        }
        buf.writeVarInt(bytes.length);
        buf.writeBytes(bytes);
    }

    public static String readUtf8(FriendlyByteBuf buf) {
        int len = buf.readVarInt();
        if (len < 0 || len > MAX_BYTES) {
            throw new EncoderException("Invalid payload length: " + len);
        }
        byte[] bytes = new byte[len];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
