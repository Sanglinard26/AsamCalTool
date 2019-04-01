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

public final class AxisPts {

    private String name;
    @SuppressWarnings("unused")
    private String longIdentifier;
    private String adress; // 4-byte unsigned integer
    @SuppressWarnings("unused")
    private String inputQuantity; // Reference to INPUT_QUANTITY
    private String deposit; // Reference to RECORLAYOUT
    @SuppressWarnings("unused")
    private float maxDiff;
    private String conversion; // Reference to COMPUTMETHOD
    private int maxAxisPoints;
    @SuppressWarnings("unused")
    private float lowerLimit;
    @SuppressWarnings("unused")
    private float upperLimit;

    private Values values;

    private CompuMethod compuMethod;
    private RecordLayout recordLayout;

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public AxisPts(List<String> parameters) {

        initOptionalsParameters();

        parameters.remove(0); // Remove /begin
        parameters.remove(0); // Remove AXIS_PTS

        if (parameters.size() == 1 || parameters.size() >= 9) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.name = parameters.get(n);
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
                            case "DEPOSIT":
                                optionalsParameters.put(DEPOSIT, parameters.get(nPar + 1));
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
                    n = parameters.size();
                    break;
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

    private final void initOptionalsParameters() {
        optionalsParameters = new HashMap<SecondaryKeywords, Object>();
        optionalsParameters.put(ANNOTATION, null);
        optionalsParameters.put(BYTE_ORDER, null); // ToDo
        optionalsParameters.put(CALIBRATION_ACCESS, null); // ToDo
        optionalsParameters.put(COMPARISON_QUANTITY, null); // ToDo
        optionalsParameters.put(DEPOSIT, null); // ToDo
        optionalsParameters.put(DISPLAY_IDENTIFIER, null);
        optionalsParameters.put(ECU_ADDRESS_EXTENSION, null); // ToDo
        optionalsParameters.put(EXTENDED_LIMITS, null); // ToDo
        optionalsParameters.put(FORMAT, null);
        optionalsParameters.put(MAX_REFRESH, null);
        optionalsParameters.put(MONOTONY, null);
        optionalsParameters.put(PHYS_UNIT, null);
        optionalsParameters.put(READ_ONLY, null); // Par defaut
        optionalsParameters.put(REF_MEMORY_SEGMENT, null);
        optionalsParameters.put(STEP_SIZE, null);
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

    public String getValues() {

        StringBuilder sb = new StringBuilder("\n");

        for (short y = 0; y < values.getDimY(); y++) {
            for (short x = 0; x < values.getDimX(); x++) {
                sb.append(values.getValue(y, x) + " | ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public void setValues(Values values) {
        this.values = values;
    }

    public final String getFormat() {
        Object oAxisPtsDisplayFormat = optionalsParameters.get(FORMAT);
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

    public final String getDepositMode() {
        Object oDeposit = optionalsParameters.get(DEPOSIT);
        return oDeposit != null ? oDeposit.toString() : "";
    }

    public final void assignComputMethod(HashMap<String, CompuMethod> compuMethods) {

        this.compuMethod = compuMethods.get(this.conversion);
    }

    public final void assignRecordLayout(HashMap<String, RecordLayout> recordLayouts) {

        this.recordLayout = recordLayouts.get(this.deposit);
    }

}
