/*
 * Creation : 6 mars 2019
 */
package association;

import java.nio.ByteBuffer;

import a2lobject.A2l;
import a2lobject.Characteristic;
import a2lobject.CompuMethod;
import a2lobject.ModPar;
import a2lobject.RecordLayout.FncValues;
import constante.DataType;
import constante.SecondaryKeywords;
import hex.IntelHex;

public final class Association {

    public static final void combine(A2l a2l, IntelHex hex) {

        // Check EPK
        ModPar modPar = a2l.getModPar();
        String addressEPK = ((String) modPar.getOptionalsParameters().get(SecondaryKeywords.ADDR_EPK));

        long lAdressEPK = Long.decode(addressEPK);

        String mEPK = (String) modPar.getOptionalsParameters().get(SecondaryKeywords.EPK);

        String data = hex.readString((int) lAdressEPK, mEPK.length());

        System.out.println("Validation EPK : " + mEPK.equals(data));

        for (Characteristic characteristic : a2l.getCharacteristics()) {
            switch (characteristic.getType()) {
            case VALUE:
                // System.out.println(characteristic);
                FncValues fncValues = (FncValues) characteristic.getRecordLayout().getOptionalsParameters().get(SecondaryKeywords.FNC_VALUES);
                CompuMethod compuMethod = characteristic.getCompuMethod();

                if (fncValues.getDataType().compareTo(DataType.UBYTE) == 0) {
                    long adress = Long.decode(characteristic.getAdress());
                    System.out.println(characteristic + " = " + compuMethod.compute(readUInt8(ByteBuffer.wrap(hex.readBytes((int) adress, 1)))));
                }
                break;

            default:
                break;
            }
        }

        //

    }

    public static byte readUInt8(ByteBuffer bb) {
        return (byte) (bb.get() & 0xff);
    }

}
