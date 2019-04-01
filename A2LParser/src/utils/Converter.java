/*
 * Creation : 10 mars 2019
 */
package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import constante.DataType;
import hex.IntelHex;

public final class Converter {

    private static final short readUBYTE(ByteBuffer bb) {
        return (short) (bb.get() & 0xff);
    }

    private static final byte readSBYTE(ByteBuffer bb) {
        return bb.get();
    }

    private static final long readULONG(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return bb.getInt() & 0xffffffffL;
    }

    private static final int readSLONG(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return bb.getInt();
    }

    private static final int readUWORD(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return bb.getShort() & 0xffff;
    }

    private static final short readSWORD(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return bb.getShort();
    }

    private static final float readFLOAT32IEEE(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return bb.getFloat();
    }

    private static final double readFLOAT64IEEE(ByteBuffer bb, ByteOrder byteOrder) {
        bb.order(byteOrder);
        return bb.getDouble();
    }

    public static final double readHexValue(IntelHex hex, long adress, DataType dataType, ByteOrder byteOrder) {
        return readHexValues(hex, adress, dataType, byteOrder, 1)[0];
    }

    public static final double[] readHexValues(IntelHex hex, long adress, DataType dataType, ByteOrder byteOrder, int nbValue) {

        byte[] byteValues;
        final double[] hexValues = new double[nbValue];
        final int nbByte = dataType.getNbByte();

        for (int nValue = 0; nValue < nbValue; nValue++) {
            byteValues = hex.readBytes(adress + (nValue * nbByte), nbByte);
            if (byteValues.length > 0) {
                switch (dataType) {
                case UBYTE:
                    hexValues[nValue] = Converter.readUBYTE(ByteBuffer.wrap(byteValues));
                    break;
                case SBYTE:
                    hexValues[nValue] = Converter.readSBYTE(ByteBuffer.wrap(byteValues));
                    break;
                case UWORD:
                    hexValues[nValue] = Converter.readUWORD(ByteBuffer.wrap(byteValues), byteOrder);
                    break;
                case SWORD:
                    hexValues[nValue] = Converter.readSWORD(ByteBuffer.wrap(byteValues), byteOrder);
                    break;
                case ULONG:
                    hexValues[nValue] = Converter.readULONG(ByteBuffer.wrap(byteValues), byteOrder);
                    break;
                case SLONG:
                    hexValues[nValue] = Converter.readSLONG(ByteBuffer.wrap(byteValues), byteOrder);
                    break;
                case FLOAT32_IEEE:
                    hexValues[nValue] = Converter.readFLOAT32IEEE(ByteBuffer.wrap(byteValues), byteOrder);
                    break;
                case FLOAT64_IEEE:
                    hexValues[nValue] = Converter.readFLOAT64IEEE(ByteBuffer.wrap(byteValues), byteOrder);
                    break;
                default:
                    // Nothing
                    break;
                }
            }
        }
        return hexValues;
    }

}
