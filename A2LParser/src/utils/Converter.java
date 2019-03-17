/*
 * Creation : 10 mars 2019
 */
package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import constante.DataType;
import hex.IntelHex;

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

    public static double readHexValue(IntelHex hex, long adress, DataType dataType, ByteOrder byteOrder) {
        return readHexValues(hex, adress, dataType, byteOrder, 1)[0];
    }

    public static double[] readHexValues(IntelHex hex, long adress, DataType dataType, ByteOrder byteOrder, int nbValue) {

        byte[] byteValues;
        double[] hexValues = new double[nbValue];
        int nbByte = dataType.getNbByte();

        switch (dataType) {
        case UBYTE:
            for (int nValue = 0; nValue < nbValue; nValue++) {
                byteValues = hex.readBytes(adress + nValue, nbByte);
                if (byteValues.length > 0) {
                    hexValues[nValue] = Converter.readUBYTE(ByteBuffer.wrap(byteValues));
                }
            }
            break;
        case SBYTE:
            for (int nValue = 0; nValue < nbValue; nValue++) {
                byteValues = hex.readBytes(adress + nValue, nbByte);
                if (byteValues.length > 0) {
                    hexValues[nValue] = Converter.readSBYTE(ByteBuffer.wrap(byteValues));
                }
            }
            break;
        case UWORD:
            for (int nValue = 0; nValue < nbValue; nValue++) {
                byteValues = hex.readBytes(adress + (nValue * nbByte), nbByte);
                if (byteValues.length > 0) {
                    hexValues[nValue] = Converter.readUWORD(ByteBuffer.wrap(byteValues), byteOrder);
                }
            }
            break;
        case SWORD:
            for (int nValue = 0; nValue < nbValue; nValue++) {
                byteValues = hex.readBytes(adress + (nValue * nbByte), nbByte);
                if (byteValues.length > 0) {
                    hexValues[nValue] = Converter.readSWORD(ByteBuffer.wrap(byteValues), byteOrder);
                }
            }
            break;
        case ULONG:
            for (int nValue = 0; nValue < nbValue; nValue++) {
                byteValues = hex.readBytes(adress + (nValue * nbByte), nbByte);
                if (byteValues.length > 0) {
                    hexValues[nValue] = Converter.readULONG(ByteBuffer.wrap(byteValues), byteOrder);
                }
            }
            break;
        case SLONG:
            for (int nValue = 0; nValue < nbValue; nValue++) {
                byteValues = hex.readBytes(adress + (nValue * nbByte), nbByte);
                if (byteValues.length > 0) {
                    hexValues[nValue] = Converter.readSLONG(ByteBuffer.wrap(byteValues), byteOrder);
                }
            }
            break;
        case FLOAT32_IEEE:
            for (int nValue = 0; nValue < nbValue; nValue++) {
                byteValues = hex.readBytes(adress + (nValue * nbByte), nbByte);
                if (byteValues.length > 0) {
                    hexValues[nValue] = Converter.readFLOAT32IEEE(ByteBuffer.wrap(byteValues), byteOrder);
                }
            }
            break;
        default:
            break;
        }

        return hexValues;
    }

}
