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

import constante.SecondaryKeywords;

public final class AxisPts extends AdjustableObject {

    @SuppressWarnings("unused")
    private String inputQuantity; // Reference to INPUT_QUANTITY
    private int maxAxisPoints;

    public AxisPts(List<String> parameters) {

        initOptionalsParameters();

        parameters.remove(0); // Remove /begin
        parameters.remove(0); // Remove AXIS_PTS

        if (parameters.size() == 1 || parameters.size() >= 9) {

            this.name = parameters.get(0);
            this.longIdentifier = parameters.get(1);
            this.adress = parameters.get(2);
            this.inputQuantity = parameters.get(3);
            this.deposit = parameters.get(4);
            this.maxDiff = Float.parseFloat(parameters.get(5));
            this.conversion = parameters.get(6);
            this.maxAxisPoints = Integer.parseInt(parameters.get(7));
            this.lowerLimit = Float.parseFloat(parameters.get(8));
            this.upperLimit = Float.parseFloat(parameters.get(9));

            int n = 10;

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
                        nPar+=1;
                        break;
                    case "DISPLAY_IDENTIFIER":
                        optionalsParameters.put(DISPLAY_IDENTIFIER, parameters.get(nPar + 1));
                        nPar+=1;
                        break;
                    case "FORMAT":
                        optionalsParameters.put(FORMAT, parameters.get(nPar + 1) + "f");
                        nPar+=1;
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

    public int getMaxAxisPoints() {
        return maxAxisPoints;
    }

    public final String getDepositMode() {
        Object oDeposit = optionalsParameters.get(DEPOSIT);
        return oDeposit != null ? oDeposit.toString() : "";
    }

    public final String[] getStringValues() {
        String[] strValues = new String[this.values.getDimX()];
        for (int i = 0; i < strValues.length; i++) {
            strValues[i] = this.values.getValue(0, i);
        }

        return strValues;
    }

    @Override
    public final void assignComputMethod(HashMap<String, CompuMethod> compuMethods) {
        this.compuMethod = compuMethods.get(this.conversion);
    }

    @Override
    public int compareTo(AdjustableObject o) {
        return this.name.compareToIgnoreCase(o.toString());
    }

	@Override
	public String[] getUnit() {
		return new String[]{this.compuMethod.getUnit()};
	}

}
