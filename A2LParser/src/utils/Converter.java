/*
 * Creation : 10 mars 2019
 */
package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class Converter {

    public static short readUBYTE(ByteBuffer bb) {
        return (short) (bb.get() & 0xff);
    }

    public static byte readSBYTE(ByteBuffer bb) {
        return bb.get();
    }

    public static int readULONG(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return (bb.getInt() & 0xffffffff);
    }

    public static int readSLONG(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return bb.getInt();
    }

    public static int readUWORD(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return bb.getShort() & 0xffff;
    }

    public static short readSWORD(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return bb.getShort();
    }

    public static float readFLOAT32IEEE(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return bb.getFloat();
    }
}
