/*
 * Creation : 6 mars 2019
 */
package association;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import a2lobject.A2l;
import a2lobject.AxisDescr;
import a2lobject.AxisDescr.Attribute;
import a2lobject.AxisPts;
import a2lobject.Characteristic;
import a2lobject.CompuMethod;
import a2lobject.FixAxisPar;
import a2lobject.FixAxisParDist;
import a2lobject.ModCommon;
import a2lobject.ModPar;
import a2lobject.RecordLayout.AxisPtsX;
import a2lobject.RecordLayout.FncValues;
import a2lobject.RecordLayout.NoAxisPtsX;
import constante.DataType;
import constante.SecondaryKeywords;
import hex.IntelHex;
import utils.Converter;

public final class Association {

    public static final boolean combine(A2l a2l, IntelHex hex) {

        // Check EPK
        ModPar modPar = a2l.getModPar();
        String addressEPK = ((String) modPar.getOptionalsParameters().get(SecondaryKeywords.ADDR_EPK));

        if (addressEPK != null) {
            long lAdressEPK = Long.decode(addressEPK);

            String mEPK = (String) modPar.getOptionalsParameters().get(SecondaryKeywords.EPK);

            String data = hex.readString((int) lAdressEPK, mEPK.length());

            System.out.println("Validation EPK : " + mEPK.equals(data));
        }

        ModCommon modCommon = a2l.getModCommon();
        String sByteOrder = (String) modCommon.getOptionalsParameters().get(SecondaryKeywords.BYTE_ORDER);
        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

        if (sByteOrder != null) {
            if (sByteOrder.equals("MSB_LAST") || sByteOrder.equals("BIG_ENDIAN")) {
                byteOrder = ByteOrder.LITTLE_ENDIAN;
            } else {
                byteOrder = ByteOrder.BIG_ENDIAN;
            }
        }

        for (Characteristic characteristic : a2l.getCharacteristics()) {

            FncValues fncValues = (FncValues) characteristic.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.FNC_VALUES);
            CompuMethod compuMethod = characteristic.getCompuMethod();

            long adress = Long.decode(characteristic.getAdress());

            byte[] byteValues;

            switch (characteristic.getType()) {
            case VALUE:

                switch (fncValues.getDataType()) {
                case UBYTE:
                    byteValues = hex.readBytes((int) adress, 1);
                    if (byteValues.length > 0) {
                        System.out.println(characteristic + " = " + compuMethod.compute(Converter.readUBYTE(ByteBuffer.wrap(byteValues))));
                    }
                    break;
                case SBYTE:
                    byteValues = hex.readBytes((int) adress, 1);
                    if (byteValues.length > 0) {
                        System.out.println(characteristic + " = " + compuMethod.compute(Converter.readSBYTE(ByteBuffer.wrap(byteValues))));
                    }
                    break;
                case UWORD:
                    byteValues = hex.readBytes((int) adress, 2);
                    if (byteValues.length > 0) {
                        System.out.println(characteristic + " = " + compuMethod.compute(Converter.readUWORD(ByteBuffer.wrap(byteValues), byteOrder)));
                    }
                    break;
                case SWORD:
                    byteValues = hex.readBytes((int) adress, 2);
                    if (byteValues.length > 0) {
                        System.out.println(characteristic + " = " + compuMethod.compute(Converter.readSWORD(ByteBuffer.wrap(byteValues), byteOrder)));
                    }
                    break;

                case ULONG:
                    byteValues = hex.readBytes((int) adress, 4);
                    if (byteValues.length > 0) {
                        System.out.println(characteristic + " = " + compuMethod.compute(Converter.readULONG(ByteBuffer.wrap(byteValues), byteOrder)));
                    }
                    break;
                case SLONG:
                    byteValues = hex.readBytes((int) adress, 4);
                    if (byteValues.length > 0) {
                        System.out.println(characteristic + " = " + compuMethod.compute(Converter.readSLONG(ByteBuffer.wrap(byteValues), byteOrder)));
                    }
                    break;
                case FLOAT32_IEEE:
                    byteValues = hex.readBytes((int) adress, 4);
                    if (byteValues.length > 0) {
                        System.out.println(
                                characteristic + " = " + compuMethod.compute(Converter.readFLOAT32IEEE(ByteBuffer.wrap(byteValues), byteOrder)));
                    }
                    break;

                default:
                    break;
                }

                break;
            case ASCII:

                if (fncValues.getDataType().compareTo(DataType.UBYTE) == 0) {

                    Object oByte = characteristic.getOptionalsParameters().get(SecondaryKeywords.NUMBER);
                    int nByte;

                    if (oByte == null) {
                        oByte = characteristic.getOptionalsParameters().get(SecondaryKeywords.MATRIX_DIM);
                        nByte = (int) ((Object[]) oByte)[0];
                    } else {
                        nByte = (int) oByte;
                    }

                    String ascii = hex.readString((int) adress, nByte);
                    if (ascii != null) {
                        System.out.println(characteristic + " = " + ascii);
                    }

                }
                break;

            case CURVE:

                System.out.println(characteristic + " = ");

                AxisDescr axisDescr = characteristic.getAxisDescrs().get(0);
                int nbValue = axisDescr.getMaxAxisPoints();

                boolean isStdAxis = false;

                switch (axisDescr.getAttribute()) {
                case FIX_AXIS:
                    Set<Entry<SecondaryKeywords, Object>> entrySet = axisDescr.getOptionalsParameters().entrySet();
                    Iterator<Entry<SecondaryKeywords, Object>> it = entrySet.iterator();

                    System.out.print(Attribute.FIX_AXIS.name() + "_X = ");

                    while (it.hasNext()) {
                        Map.Entry<SecondaryKeywords, Object> entry = it.next();
                        if (entry.getValue() instanceof FixAxisParDist) {
                            FixAxisParDist axisDist = (FixAxisParDist) entry.getValue();
                            for (int n = 0; n < axisDist.getNumberapo(); n++) {
                                System.out.print(axisDist.compute(n) + " | ");
                            }
                            System.out.print("\n");
                            break;
                        } else if (entry.getValue() instanceof FixAxisPar) {
                            FixAxisPar axisDist = (FixAxisPar) entry.getValue();
                            for (int n = 0; n < axisDist.getNumberapo(); n++) {
                                System.out.print(axisDist.compute(n) + " | ");
                            }
                            System.out.print("\n");
                        }
                    }
                    break;

                case STD_AXIS:
                    isStdAxis = true;
                    AxisPtsX axisPtsXStdAxis = (AxisPtsX) characteristic.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.AXIS_PTS_X);
                    NoAxisPtsX noAxisPtsX = (NoAxisPtsX) characteristic.getRecordLayout().getOptionalsParameters()
                            .get(SecondaryKeywords.NO_AXIS_PTS_X);

                    int posNoAxisPtsX = noAxisPtsX.getPosition();
                    int posAxisPtsX = axisPtsXStdAxis.getPosition();
                    int posFncValues = fncValues.getPosition();

                    int nbAxisPts = 0;

                    switch (noAxisPtsX.getDataType()) {
                    case UBYTE:
                        byteValues = hex.readBytes((int) adress, 1);
                        if (byteValues.length > 0) {
                            nbAxisPts = Converter.readUBYTE(ByteBuffer.wrap(byteValues));
                            adress += 1;
                        }
                        break;
                    case SBYTE:
                        byteValues = hex.readBytes((int) adress, 1);
                        if (byteValues.length > 0) {
                            nbAxisPts = Converter.readSBYTE(ByteBuffer.wrap(byteValues));
                            adress += 1;
                        }
                        break;
                    case UWORD:
                        byteValues = hex.readBytes((int) adress, 2);
                        if (byteValues.length > 0) {
                            nbAxisPts = Converter.readUWORD(ByteBuffer.wrap(byteValues), byteOrder);
                            adress += 2;
                        }
                        break;
                    case SWORD:
                        byteValues = hex.readBytes((int) adress, 2);
                        if (byteValues.length > 0) {
                            nbAxisPts = Converter.readSWORD(ByteBuffer.wrap(byteValues), byteOrder);
                            adress += 2;
                        }
                        break;

                    case ULONG:
                        byteValues = hex.readBytes((int) adress, 4);
                        if (byteValues.length > 0) {
                            nbAxisPts = Converter.readULONG(ByteBuffer.wrap(byteValues), byteOrder);
                            adress += 4;
                        }
                        break;
                    case SLONG:
                        byteValues = hex.readBytes((int) adress, 4);
                        if (byteValues.length > 0) {
                            nbAxisPts = Converter.readSLONG(ByteBuffer.wrap(byteValues), byteOrder);
                            adress += 4;
                        }
                        break;
                    case FLOAT32_IEEE:
                        byteValues = hex.readBytes((int) adress, 4);
                        if (byteValues.length > 0) {
                            nbAxisPts = (int) Converter.readFLOAT32IEEE(ByteBuffer.wrap(byteValues), byteOrder);
                            adress += 4;
                        }
                        break;

                    default:
                        break;
                    }

                    nbValue = nbAxisPts;

                    System.out.print(Attribute.STD_AXIS.name() + "_X = ");

                    AxisDescr axisDescrStdAxis = characteristic.getAxisDescrs().get(0);
                    CompuMethod compuMethodStdAxis = axisDescrStdAxis.getCompuMethod();

                    switch (axisPtsXStdAxis.getDataType()) {
                    case UBYTE:

                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adress + nValue, 1);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodStdAxis.compute(Converter.readUBYTE(ByteBuffer.wrap(byteValues))) + " | ");
                                //
                            }
                        }
                        adress += nbValue * 1;
                        System.out.print("\n");

                        break;
                    case SBYTE:

                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adress + nValue, 1);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodStdAxis.compute(Converter.readSBYTE(ByteBuffer.wrap(byteValues))) + " | ");
                                //
                            }
                        }
                        adress += nbValue * 1;
                        System.out.print("\n");
                        break;
                    case UWORD:
                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adress + (nValue * 2), 2);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodStdAxis.compute(Converter.readUWORD(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                                //
                            }
                        }
                        adress += nbValue * 2;
                        System.out.print("\n");
                        break;
                    case SWORD:
                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adress + (nValue * 2), 2);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodStdAxis.compute(Converter.readSWORD(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                                //
                            }
                        }
                        adress += nbValue * 2;
                        System.out.print("\n");
                        break;
                    case ULONG:
                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adress + (nValue * 4), 4);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodStdAxis.compute(Converter.readULONG(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                                //
                            }
                        }
                        adress += nbValue * 4;
                        System.out.print("\n");
                        break;
                    case SLONG:
                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adress + (nValue * 4), 4);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodStdAxis.compute(Converter.readSLONG(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                                //
                            }
                        }
                        adress += nbValue * 4;
                        System.out.print("\n");
                        break;
                    case FLOAT32_IEEE:
                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adress + (nValue * 4), 4);
                            if (byteValues.length > 0) {
                                System.out
                                        .print(compuMethodStdAxis.compute(Converter.readFLOAT32IEEE(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                                //
                            }
                        }
                        adress += nbValue * 4;
                        System.out.print("\n");
                        break;
                    default:
                        break;
                    }

                    break;

                case COM_AXIS:

                    AxisPts axisPts = axisDescr.getAxisPts();
                    AxisPtsX axisPtsX = (AxisPtsX) axisPts.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.AXIS_PTS_X);
                    CompuMethod compuMethodAxis = axisPts.getCompuMethod();
                    long adressAxis = Long.decode(axisPts.getAdress());

                    System.out.print(Attribute.COM_AXIS.name() + "_X = ");

                    switch (axisPtsX.getDataType()) {
                    case UBYTE:

                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adressAxis + nValue, 1);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodAxis.compute(Converter.readUBYTE(ByteBuffer.wrap(byteValues))) + " | ");
                            }
                        }
                        System.out.print("\n");

                        break;
                    case SBYTE:

                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adressAxis + nValue, 1);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodAxis.compute(Converter.readSBYTE(ByteBuffer.wrap(byteValues))) + " | ");
                            }
                        }
                        System.out.print("\n");
                        break;
                    case UWORD:
                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adressAxis + (nValue * 2), 2);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodAxis.compute(Converter.readUWORD(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                            }
                        }
                        System.out.print("\n");
                        break;
                    case SWORD:
                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adressAxis + (nValue * 2), 2);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodAxis.compute(Converter.readSWORD(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                            }
                        }
                        System.out.print("\n");
                        break;
                    case ULONG:
                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adressAxis + (nValue * 4), 4);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodAxis.compute(Converter.readULONG(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                            }
                        }
                        System.out.print("\n");
                        break;
                    case SLONG:
                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adressAxis + (nValue * 4), 4);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodAxis.compute(Converter.readSLONG(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                            }
                        }
                        System.out.print("\n");
                        break;
                    case FLOAT32_IEEE:
                        for (int nValue = 0; nValue < nbValue; nValue++) {
                            byteValues = hex.readBytes((int) adressAxis + (nValue * 4), 4);
                            if (byteValues.length > 0) {
                                System.out.print(compuMethodAxis.compute(Converter.readFLOAT32IEEE(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                            }
                        }
                        System.out.print("\n");
                        break;
                    default:
                        break;
                    }

                    break;

                default:
                    break;
                }

                System.out.print("         Z = ");

                switch (fncValues.getDataType()) {
                case UBYTE:

                    for (int nValue = 0; nValue < nbValue; nValue++) {
                        byteValues = hex.readBytes((int) adress + nValue, 1);
                        if (byteValues.length > 0) {
                            System.out.print(compuMethod.compute(Converter.readUBYTE(ByteBuffer.wrap(byteValues))) + " | ");
                        }
                    }
                    System.out.print("\n");

                    break;
                case SBYTE:

                    for (int nValue = 0; nValue < nbValue; nValue++) {
                        byteValues = hex.readBytes((int) adress + nValue, 1);
                        if (byteValues.length > 0) {
                            System.out.print(compuMethod.compute(Converter.readSBYTE(ByteBuffer.wrap(byteValues))) + " | ");
                        }
                    }
                    System.out.print("\n");
                    break;
                case UWORD:
                    for (int nValue = 0; nValue < nbValue; nValue++) {
                        byteValues = hex.readBytes((int) adress + (nValue * 2), 2);
                        if (byteValues.length > 0) {
                            System.out.print(compuMethod.compute(Converter.readUWORD(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                        }
                    }
                    System.out.print("\n");
                    break;
                case SWORD:
                    for (int nValue = 0; nValue < nbValue; nValue++) {
                        byteValues = hex.readBytes((int) adress + (nValue * 2), 2);
                        if (byteValues.length > 0) {
                            System.out.print(compuMethod.compute(Converter.readSWORD(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                        }
                    }
                    System.out.print("\n");
                    break;
                case ULONG:
                    for (int nValue = 0; nValue < nbValue; nValue++) {
                        byteValues = hex.readBytes((int) adress + (nValue * 4), 4);
                        if (byteValues.length > 0) {
                            System.out.print(compuMethod.compute(Converter.readULONG(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                        }
                    }
                    System.out.print("\n");
                    break;
                case SLONG:
                    for (int nValue = 0; nValue < nbValue; nValue++) {
                        byteValues = hex.readBytes((int) adress + (nValue * 4), 4);
                        if (byteValues.length > 0) {
                            System.out.print(compuMethod.compute(Converter.readSLONG(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                        }
                    }
                    System.out.print("\n");
                    break;
                case FLOAT32_IEEE:
                    for (int nValue = 0; nValue < nbValue; nValue++) {
                        byteValues = hex.readBytes((int) adress + (nValue * 4), 4);
                        if (byteValues.length > 0) {
                            System.out.print(compuMethod.compute(Converter.readFLOAT32IEEE(ByteBuffer.wrap(byteValues), byteOrder)) + " | ");
                        }
                    }
                    System.out.print("\n");
                    break;
                default:
                    break;
                }

                break;

            case MAP:

                break;

            default:
                break;
            }
        }

        return true;

    }

}
