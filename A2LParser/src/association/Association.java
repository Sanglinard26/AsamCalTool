/*
 * Creation : 6 mars 2019
 */
package association;

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
import constante.IndexMode;
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

            switch (characteristic.getType()) {
            case VALUE:

                System.out.println(
                        characteristic + " = " + compuMethod.compute(Converter.readHexValue(hex, adress, fncValues.getDataType(), byteOrder)));

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
                    AxisPtsX axisPtsXStdAxis = (AxisPtsX) characteristic.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.AXIS_PTS_X);
                    NoAxisPtsX noAxisPtsX = (NoAxisPtsX) characteristic.getRecordLayout().getOptionalsParameters()
                            .get(SecondaryKeywords.NO_AXIS_PTS_X);

                    nbValue = (int) Converter.readHexValue(hex, adress, noAxisPtsX.getDataType(), byteOrder);
                    adress += noAxisPtsX.getDataType().getNbByte();

                    System.out.print(Attribute.STD_AXIS.name() + "_X = ");

                    AxisDescr axisDescrStdAxis = characteristic.getAxisDescrs().get(0);
                    CompuMethod compuMethodStdAxis = axisDescrStdAxis.getCompuMethod();

                    double[] hexValues = Converter.readHexValues(hex, adress, axisPtsXStdAxis.getDataType(), byteOrder, nbValue);
                    adress += axisPtsXStdAxis.getDataType().getNbByte() * nbValue;

                    for (double val : hexValues) {
                        System.out.print(compuMethodStdAxis.compute(val) + " | ");
                    }
                    System.out.print("\n");

                    break;

                case COM_AXIS:

                    AxisPts axisPts = axisDescr.getAxisPts();
                    AxisPtsX axisPtsX = (AxisPtsX) axisPts.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.AXIS_PTS_X);
                    CompuMethod compuMethodAxis = axisPts.getCompuMethod();
                    long adressAxis = Long.decode(axisPts.getAdress());

                    System.out.print(Attribute.COM_AXIS.name() + "_X = ");

                    double[] hexValuesComAxis = Converter.readHexValues(hex, adressAxis, axisPtsX.getDataType(), byteOrder, nbValue);

                    for (double val : hexValuesComAxis) {
                        System.out.print(compuMethodAxis.compute(val) + " | ");
                    }
                    System.out.print("\n");

                    break;

                default:
                    break;
                }

                System.out.print("         Z = ");

                double[] hexValues = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, nbValue);

                for (double val : hexValues) {
                    System.out.print(compuMethod.compute(val) + " | ");
                }
                System.out.print("\n");

                break;

            case VAL_BLK:

                System.out.println(characteristic + " = ");

                Object numberParam = characteristic.getOptionalsParameters().get(SecondaryKeywords.NUMBER);
                Object matrixDimParam = characteristic.getOptionalsParameters().get(SecondaryKeywords.MATRIX_DIM);

                IndexMode indexModeValBlk = fncValues.getIndexMode();

                int[] dim;

                if (matrixDimParam != null) {
                    Object[] arrMatrixDim = (Object[]) matrixDimParam;
                    dim = new int[] { (int) arrMatrixDim[0], (int) arrMatrixDim[1], (int) arrMatrixDim[2] };
                } else {
                    dim = new int[] { (int) numberParam };
                }

                System.out.print("X = ");
                for (int x = 0; x < dim[0]; x++) {
                    System.out.print(x + " | ");
                }

                double[] hexValuesValBlk;

                if (dim.length < 2 || dim[1] == 1) {
                    System.out.print("\n");
                    System.out.print("Z = ");

                    hexValuesValBlk = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, dim[0]);

                    for (double val : hexValuesValBlk) {
                        System.out.print(compuMethod.compute(val) + " | ");
                    }

                } else {

                    nbValue = dim[0] * dim[1];

                    hexValuesValBlk = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, nbValue);

                    int cnt = 0;

                    for (double val : hexValuesValBlk) {

                        if (cnt % dim[0] == 0) {
                            System.out.print("\nZ" + (cnt / dim[0]) + " = ");
                        }
                        System.out.print(compuMethod.compute(val) + " | ");
                        cnt++;
                    }
                }

                System.out.print("\n");

                break;

            case MAP:

                System.out.println(characteristic + " = ");

                int nbValueMap = 0;
                int[] dimMap = new int[2];

                IndexMode indexModeMap = fncValues.getIndexMode();

                int cnt = 0;

                for (AxisDescr axis : characteristic.getAxisDescrs()) {
                    switch (axis.getAttribute()) {
                    case FIX_AXIS:
                        dimMap[cnt] = axis.getMaxAxisPoints();

                        Set<Entry<SecondaryKeywords, Object>> entrySet = axis.getOptionalsParameters().entrySet();
                        Iterator<Entry<SecondaryKeywords, Object>> it = entrySet.iterator();

                        if (cnt == 0) {
                            System.out.print(Attribute.FIX_AXIS.name() + "_X = ");
                        } else {
                            System.out.print(Attribute.FIX_AXIS.name() + "_Y = ");
                        }

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

                        break;
                    case COM_AXIS:

                        AxisPts axisPts = axis.getAxisPts();
                        AxisPtsX axisPtsX = (AxisPtsX) axisPts.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.AXIS_PTS_X);
                        CompuMethod compuMethodAxis = axisPts.getCompuMethod();
                        long adressAxis = Long.decode(axisPts.getAdress());

                        int nbValueAxis = axisPts.getMaxAxisPoints();

                        dimMap[cnt] = axis.getMaxAxisPoints();

                        if (cnt == 0) {
                            System.out.print(Attribute.COM_AXIS.name() + "_X = ");
                        } else {
                            System.out.print(Attribute.COM_AXIS.name() + "_Y = ");
                        }

                        double[] hexValuesComAxis = Converter.readHexValues(hex, adressAxis, axisPtsX.getDataType(), byteOrder, nbValueAxis);

                        for (double val : hexValuesComAxis) {
                            System.out.print(compuMethodAxis.compute(val) + " | ");
                        }
                        System.out.print("\n");

                        break;

                    default:
                        break;
                    }

                    cnt++;
                }

                nbValueMap = dimMap[0] * dimMap[1];

                if (nbValueMap > 0) {

                    hexValuesValBlk = Converter.readHexValues(hex, adress, fncValues.getDataType(), byteOrder, nbValueMap);

                    cnt = 0;

                    for (double val : hexValuesValBlk) {

                        if (cnt % dimMap[0] == 0) {
                            System.out.print("\nZ" + (cnt / dimMap[0]) + " = ");
                        }
                        System.out.print(compuMethod.compute(val) + " | ");
                        cnt++;
                    }

                    System.out.print("\n");
                }

                break;

            default:
                break;
            }
        }

        return true;

    }

}
