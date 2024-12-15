/*
 * Creation : 10 mars 2019
 */
package utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import constante.DataType;
import data.DataCalibration;

public final class Converter {

    private static final short readUBYTE(ByteBuffer bb) {
        return (short) (bb.get() & 0xff);
    }

    private static final byte readSBYTE(ByteBuffer bb) {
        return bb.get();
    }

    private static final long readULONG(ByteBuffer bb) {
        return bb.getInt() & 0xffffffffL;
    }

    private static final int readSLONG(ByteBuffer bb) {
        return bb.getInt();
    }

    private static final int readUWORD(ByteBuffer bb) {
        return bb.getShort() & 0xffff;
    }

    private static final short readSWORD(ByteBuffer bb) {
        return bb.getShort();
    }

    private static final float readFLOAT32IEEE(ByteBuffer bb) {
        return bb.getFloat();
    }

    private static final double readFLOAT64IEEE(ByteBuffer bb) {
        return bb.getDouble();
    }

    public static final double readHexValue(DataCalibration hex, long adress, DataType dataType, ByteOrder byteOrder) {
        return readHexValues(hex, adress, dataType, byteOrder, 1)[0];
    }

    public static final double[] readHexValues(DataCalibration hex, long adress, DataType dataType, ByteOrder byteOrder, int nbValue) {

        byte[] byteValues;
        final double[] hexValues = new double[nbValue];
        final byte nbByte = dataType.getNbByte();

        final ByteBuffer bb = ByteBuffer.allocateDirect(nbByte);
        bb.order(byteOrder);

        for (int nValue = 0; nValue < nbValue; nValue++) {
            byteValues = hex.readBytes(adress + (nValue * nbByte), nbByte);
            if (byteValues.length > 0) {

                bb.put(byteValues);
                bb.rewind();

                switch (dataType) {
                case UBYTE:
                    hexValues[nValue] = readUBYTE(bb);
                    break;
                case SBYTE:
                    hexValues[nValue] = readSBYTE(bb);
                    break;
                case UWORD:
                    hexValues[nValue] = readUWORD(bb);
                    break;
                case SWORD:
                    hexValues[nValue] = readSWORD(bb);
                    break;
                case ULONG:
                    hexValues[nValue] = readULONG(bb);
                    break;
                case SLONG:
                    hexValues[nValue] = readSLONG(bb);
                    break;
                case FLOAT32_IEEE:
                    hexValues[nValue] = readFLOAT32IEEE(bb);
                    break;
                case FLOAT64_IEEE:
                    hexValues[nValue] = readFLOAT64IEEE(bb);
                    break;
                default:
                    // Nothing
                    break;
                }

                bb.clear();
            }
        }
        return hexValues;
    }

    public static final double[] readHexValuesPairs(DataCalibration hex, long adress, DataType dataType, ByteOrder byteOrder, int nbValue) {

        byte[] byteValues;
        final double[] hexValues = new double[nbValue];
        final byte nbByte = dataType.getNbByte();

        final ByteBuffer bb = ByteBuffer.allocateDirect(nbByte);
        bb.order(byteOrder);

        for (int nValue = 0; nValue < nbValue; nValue++) {
            byteValues = hex.readBytes(adress + (nValue * 2 * nbByte), nbByte); // *2 : Patch pour ne lire une valeur par paire sur les axis_rescale
            if (byteValues.length > 0) {

                bb.put(byteValues);
                bb.rewind();

                switch (dataType) {
                case UBYTE:
                    hexValues[nValue] = readUBYTE(bb);
                    break;
                case SBYTE:
                    hexValues[nValue] = readSBYTE(bb);
                    break;
                case UWORD:
                    hexValues[nValue] = readUWORD(bb);
                    break;
                case SWORD:
                    hexValues[nValue] = readSWORD(bb);
                    break;
                case ULONG:
                    hexValues[nValue] = readULONG(bb);
                    break;
                case SLONG:
                    hexValues[nValue] = readSLONG(bb);
                    break;
                case FLOAT32_IEEE:
                    hexValues[nValue] = readFLOAT32IEEE(bb);
                    break;
                case FLOAT64_IEEE:
                    hexValues[nValue] = readFLOAT64IEEE(bb);
                    break;
                default:
                    // Nothing
                    break;
                }

                bb.clear();
            }
        }
        return hexValues;
    }

}
