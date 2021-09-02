package com.shxhzhxx.asn1;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ASN1InputStream {
    private final ByteBuffer buffer;

    public ASN1InputStream(byte[] in) {
        buffer = ByteBuffer.wrap(in);
    }

    private int read() {
        return buffer.get() & 0xFF;
    }

    public final int readTag() {
        return read();
    }

    public final int readLength() throws IOException {
        int length = read();

        if (length == 0x80) {
            throw new IOException("unsupported indefinite-length encoding");
        }

        if (length > 127) {
            int size = length & 0x7f;

            // Note: The invalid long form "0xff" (see X.690 8.1.3.5c) will be caught here
            if (size > 4) {
                throw new IOException("DER length more than 4 bytes: " + size);
            }

            length = 0;
            for (int i = 0; i < size; i++) {
                int next = read();

                length = (length << 8) + next;
            }

            if (length > buffer.remaining()) {
                throw new IOException("corrupted stream - out of bounds length found: " + length + " >= " + buffer.remaining());
            }
        }

        return length;
    }

    public final byte[] readContent(int size) {
        byte[] content = new byte[size];
        buffer.get(content);
        return content;
    }

    public final byte[] remaining() {
        byte[] remaining = new byte[buffer.remaining()];
        buffer.get(remaining);
        return remaining;
    }

    public final boolean hasRemaining() {
        return buffer.hasRemaining();
    }
}
