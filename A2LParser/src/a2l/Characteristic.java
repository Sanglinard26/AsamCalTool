/*
 * Creation : 2 mars 2018
 */
package a2l;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parametre calibrable avec les proprietes suivantes : -nom - description - type : VALUE, ASCII, VAL_BLK, CURVE, MAP, CUBOID, CUBE_4, CUBE_5 - adress
 * - record layout - computation method - upper and lower calibration limits - format
 */

public final class Characteristic {

    private String name;
    private String longIdentifier;
    private String type;
    private long adress;
    private String deposit; // Reference to RECORLAYOUT
    private float maxDiff;
    private String conversion; // Reference to COMPUTMETHOD
    private float lowerLimit;
    private float upperLimit;

    private CompuMethod compuMethod;

    private final Map<String, Object> optionalsParameters = new HashMap<String, Object>() {
        {
            put("ANNOTATION", null);
            put("AXIS_DESCR", null);
            put("BIT_MASK", null);
            put("BYTE_ORDER", null);
            put("CALIBRATION_ACCESS", null);
            put("COMPARISON_QUANTITY", null);
            put("DEPENDENT_CHARACTERISTIC", null);
            put("DISCRETE", null);
            put("DISPLAY_IDENTIFIER", null);
            put("ECU_ADDRESS_EXTENSION", null);
            put("ENCODING", null);
            put("EXTENDED_LIMITS", null);
            put("FORMAT", null);
            put("FUNCTION_LIST", null);
            put("GUARD_RAILS", null);
            put("IF_DATA", null);
            put("MAP_LIST", null);
            put("MATRIX_DIM", null);
            put("MAX_REFRESH", null);
            put("MODEL_LINK", null);
            put("NUMBER", null);
            put("PHYS_UNIT", null);
            put("READ_ONLY", null);
            put("REF_MEMORY_SEGMENT", null);
            put("STEP_SIZE", null);
            put("SYMBOL_LINK", null);
            put("VIRTUAL_CHARACTERISTIC", null);

        }
    };

    public Characteristic(List<String> parameters) {

        parameters.remove(0); // Remove /begin
        parameters.remove(0); // Remove CHARACTERISTIC

        if (parameters.size() == 1 || parameters.size() >= 9) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.name = parameters.get(n);
                    // System.out.println(this.name);
                    break;
                case 1:
                    this.longIdentifier = parameters.get(n);
                    break;
                case 2:
                    this.type = parameters.get(n);
                    break;
                case 3:
                    this.adress = Long.decode(parameters.get(n));
                    break;
                case 4:
                    this.deposit = parameters.get(n);
                    break;
                case 5:
                    this.maxDiff = Float.parseFloat(parameters.get(n));
                    break;
                case 6:
                    this.conversion = parameters.get(n);
                    break;
                case 7:
                    this.lowerLimit = Float.parseFloat(parameters.get(n));
                    break;
                case 8:
                    this.upperLimit = Float.parseFloat(parameters.get(n));
                    break;

                default: // Cas de parametres optionels

                    break;
                }
            }
        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getConversion() {
        return conversion;
    }

    public final void assignComputMethod(List<CompuMethod> compuMethods) {

        int idx = Collections.binarySearch(compuMethods, CompuMethod.createEmptyCompuMethod(conversion));

        if (idx > -1) {
            this.compuMethod = compuMethods.get(idx);
        }
    }

    public final String getInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name : " + name + "\n");
        sb.append("LongIdentifier : " + longIdentifier + "\n");
        sb.append("Type : " + type + "\n");
        sb.append("Adress : " + adress + "\n");
        sb.append("Deposit : " + deposit + "\n");
        sb.append("Conversion : " + conversion + "\n");
        sb.append("MaxDiff : " + maxDiff + "\n");
        sb.append("LowerLimit : " + lowerLimit + "\n");
        sb.append("UpperLimit : " + upperLimit + "\n");

        return sb.toString();
    }

}
