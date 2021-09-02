package com.shxhzhxx.asn1;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;


public class ASN1OutputStream implements Encodable {

    private ByteArrayOutputStream os = new ByteArrayOutputStream();

    private void writeLength(
            int length) {
        if (length > 127) {
            int size = 1;
            int val = length;

            while ((val >>>= 8) != 0) {
                size++;
            }

            write((byte) (size | 0x80));

            for (int i = (size - 1) * 8; i >= 0; i -= 8) {
                write((byte) (length >> i));
            }
        } else {
            write((byte) length);
        }
    }

    private void write(int b) {
        os.write(b);
    }

    public final void write(byte[] bytes) {
        os.write(bytes, 0, bytes.length);
    }

    private void writeEncoded(int tag, byte[] contents) {
        write(tag);
        writeLength(contents.length);
        write(contents);
    }

    public final ASN1OutputStream writeInteger(long value) {
        writeEncoded(Constants.TAG_INTEGER, BigInteger.valueOf(value).toByteArray());
        return this;
    }

    public final ASN1OutputStream writeOctetString(byte[] value) {
        writeEncoded(Constants.TAG_OCTET_STRING, value);
        return this;
    }

    public final ASN1OutputStream writeNull() {
        writeEncoded(Constants.TAG_NULL, new byte[0]);
        return this;
    }

    public final ASN1OutputStream writeSequence(byte[] contents) {
        writeEncoded(Constants.TAG_SEQUENCE, contents);
        return this;
    }

    public final ASN1OutputStream writeSet(byte[] contents) {
        writeEncoded(Constants.TAG_SET, contents);
        return this;
    }

    public final byte[] getEncoded() {
        return os.toByteArray();
    }
}
