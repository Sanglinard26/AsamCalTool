/*
 * Creation : 2 mars 2018
 */
package a2lobject;

import static constante.SecondaryKeywords.ANNOTATION;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.CALIBRATION_ACCESS;
import static constante.SecondaryKeywords.COMPARISON_QUANTITY;
import static constante.SecondaryKeywords.DEPOSIT;
import static constante.SecondaryKeywords.DISPLAY_IDENTIFIER;
import static constante.SecondaryKeywords.ECU_ADDRESS_EXTENSION;
import static constante.SecondaryKeywords.EXTENDED_LIMITS;
import static constante.SecondaryKeywords.FORMAT;
import static constante.SecondaryKeywords.MAX_REFRESH;
import static constante.SecondaryKeywords.MONOTONY;
import static constante.SecondaryKeywords.PHYS_UNIT;
import static constante.SecondaryKeywords.READ_ONLY;
import static constante.SecondaryKeywords.REF_MEMORY_SEGMENT;
import static constante.SecondaryKeywords.STEP_SIZE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.ConversionType;
import constante.SecondaryKeywords;

/**
 * Parametre calibrable avec les proprietes suivantes : -nom - description - type : VALUE, ASCII, VAL_BLK, CURVE, MAP, CUBOID, CUBE_4, CUBE_5 - adress
 * - record layout - computation method - upper and lower calibration limits - format
 */

public final class AxisPts {

    private String name;
    private String longIdentifier;
    private String adress; // 4-byte unsigned integer
    private String inputQuantity; // Reference to INPUT_QUANTITY
    private String deposit; // Reference to RECORLAYOUT
    private float maxDiff;
    private String conversion; // Reference to COMPUTMETHOD
    private int maxAxisPoints;
    private float lowerLimit;
    private float upperLimit;

    private CompuMethod compuMethod;
    private RecordLayout recordLayout;

    private final Map<SecondaryKeywords, Object> optionalsParameters = new HashMap<SecondaryKeywords, Object>() {
        private static final long serialVersionUID = 1L;

        {
            put(ANNOTATION, null);
            put(BYTE_ORDER, null); // ToDo
            put(CALIBRATION_ACCESS, null); // ToDo
            put(COMPARISON_QUANTITY, null); // ToDo
            put(DEPOSIT, null); // ToDo
            put(DISPLAY_IDENTIFIER, null);
            put(ECU_ADDRESS_EXTENSION, null); // ToDo
            put(EXTENDED_LIMITS, null); // ToDo
            put(FORMAT, null);
            put(MAX_REFRESH, null);
            put(MONOTONY, null);
            put(PHYS_UNIT, null);
            put(READ_ONLY, null); // Par defaut
            put(REF_MEMORY_SEGMENT, null);
            put(STEP_SIZE, null);
        }
    };

    public AxisPts(List<String> parameters) {

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
                    this.adress = parameters.get(n);
                    break;
                case 3:
                    this.inputQuantity = parameters.get(n);
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
                    this.maxAxisPoints = Integer.parseInt(parameters.get(n));
                    break;
                case 8:
                    this.lowerLimit = Float.parseFloat(parameters.get(n));
                    break;
                case 9:
                    this.upperLimit = Float.parseFloat(parameters.get(n));
                    break;

                default: // Cas de parametres optionels

                    Set<SecondaryKeywords> keys = optionalsParameters.keySet();
                    for (int nPar = n; nPar < parameters.size(); nPar++) {
                        if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                            switch (parameters.get(nPar)) {
                            case "ANNOTATION":
                                n = nPar + 1;
                                do {
                                } while (!parameters.get(++nPar).equals("ANNOTATION"));
                                optionalsParameters.put(ANNOTATION, new Annotation(parameters.subList(n, nPar - 3)));
                                n = nPar + 1;
                                break;
                            case "DISPLAY_IDENTIFIER":
                                optionalsParameters.put(DISPLAY_IDENTIFIER, parameters.get(nPar + 1));
                                break;
                            case "FORMAT":
                                optionalsParameters.put(FORMAT, new Format(parameters.get(nPar + 1).toString()));
                                break;
                            case "PHYS_UNIT":

                                break;
                            case "READ_ONLY":
                                optionalsParameters.put(READ_ONLY, true);
                                break;
                            default:
                                break;
                            }
                        }
                    }
                }
            }

            // On vide la MAP de parametre non utilise
            Iterator<Map.Entry<SecondaryKeywords, Object>> iter = optionalsParameters.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<SecondaryKeywords, Object> entry = iter.next();
                if (entry.getValue() == null) {
                    iter.remove();
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

    public CompuMethod getCompuMethod() {
        return compuMethod;
    }

    public RecordLayout getRecordLayout() {
        return recordLayout;
    }

    public long getAdress() {
        return Long.parseLong(adress.substring(2), 16);
    }

    public int getMaxAxisPoints() {
        return maxAxisPoints;
    }

    public final String getFormat() {
        Object oAxisPtsDisplayFormat = optionalsParameters.get(SecondaryKeywords.FORMAT);
        String displayFormat;

        if (compuMethod.getConversionType().compareTo(ConversionType.RAT_FUNC) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.IDENTICAL) == 0
                || compuMethod.getConversionType().compareTo(ConversionType.LINEAR) == 0) {
            if (oAxisPtsDisplayFormat == null) {
                displayFormat = compuMethod.getFormat() + "f";
            } else {
                displayFormat = oAxisPtsDisplayFormat.toString() + "f";
            }
            if (displayFormat.charAt(1) == '0') {
                displayFormat = displayFormat.replaceFirst("0", "");
            }
            return displayFormat;
        }
        return "%16.16";
    }

    public final void assignComputMethod(HashMap<String, CompuMethod> compuMethods) {

        this.compuMethod = compuMethods.get(this.conversion);
    }

    public final void assignRecordLayout(HashMap<String, RecordLayout> recordLayouts) {

        this.recordLayout = recordLayouts.get(this.deposit);
    }

}
