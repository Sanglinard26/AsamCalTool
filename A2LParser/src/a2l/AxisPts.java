/*
 * Creation : 2 mars 2018
 */
package a2l;

import static constante.SecondaryKeywords.ANNOTATION;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.CALIBRATION_ACCESS;
import static constante.SecondaryKeywords.COMPARISON_QUANTITY;
import static constante.SecondaryKeywords.DEPOSIT;
import static constante.SecondaryKeywords.DISPLAY_IDENTIFIER;
import static constante.SecondaryKeywords.ECU_ADDRESS_EXTENSION;
import static constante.SecondaryKeywords.EXTENDED_LIMITS;
import static constante.SecondaryKeywords.FORMAT;
import static constante.SecondaryKeywords.MONOTONY;
import static constante.SecondaryKeywords.PHYS_UNIT;
import static constante.SecondaryKeywords.READ_ONLY;

import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import constante.ConversionType;
import constante.SecondaryKeywords;

public final class AxisPts extends AdjustableObject {

    @SuppressWarnings("unused")
    private String inputQuantity; // Reference to INPUT_QUANTITY
    private short maxAxisPoints;

    public AxisPts(List<String> parameters, int beginLine, int endLine) {

        initOptionalsParameters();

        build(parameters, beginLine, endLine);
    }

    private final void initOptionalsParameters() {
        optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
        optionalsParameters.put(ANNOTATION, null);
        optionalsParameters.put(BYTE_ORDER, null);
        optionalsParameters.put(CALIBRATION_ACCESS, null); // ToDo
        optionalsParameters.put(COMPARISON_QUANTITY, null); // ToDo
        optionalsParameters.put(DEPOSIT, null);
        optionalsParameters.put(DISPLAY_IDENTIFIER, null);
        optionalsParameters.put(ECU_ADDRESS_EXTENSION, null); // ToDo
        optionalsParameters.put(EXTENDED_LIMITS, null); // ToDo
        optionalsParameters.put(FORMAT, null);
        optionalsParameters.put(MONOTONY, null);
        optionalsParameters.put(PHYS_UNIT, null);
        optionalsParameters.put(READ_ONLY, false); // Par defaut
    }

    @Override
    public String toString() {
        return this.name;
    }

    public final short getMaxAxisPoints() {
        return maxAxisPoints;
    }

    public final String getDepositMode() {
        Object oDeposit = optionalsParameters.get(DEPOSIT);
        return oDeposit != null ? oDeposit.toString() : "";
    }

    public final String[] getStringValues() {
        String[] strValues = new String[this.values.getDimX()];
        for (short i = 0; i < strValues.length; i++) {
            strValues[i] = this.values.getValue(0, i);
        }

        return strValues;
    }

    @Override
    public final void assignComputMethod(HashMap<String, CompuMethod> compuMethods) {
        this.compuMethod = compuMethods.get(this.conversion);
    }

    @Override
    public String[] getUnit() {
        return new String[] { this.compuMethod.getUnit() };
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

        final int nbParams = parameters.size();

        if (nbParams >= 9) {

            this.name = parameters.get(2);
            this.longIdentifier = parameters.get(3);
            this.adress = parameters.get(4);
            this.inputQuantity = parameters.get(5);
            this.deposit = parameters.get(6);
            this.maxDiff = Float.parseFloat(parameters.get(7));
            this.conversion = parameters.get(8);
            this.maxAxisPoints = (short) Integer.parseInt(parameters.get(9));
            this.lowerLimit = Float.parseFloat(parameters.get(10));
            this.upperLimit = Float.parseFloat(parameters.get(11));

            int n = 12;

            Set<SecondaryKeywords> keys = optionalsParameters.keySet();
            for (int nPar = n; nPar < nbParams; nPar++) {
                if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                    switch (parameters.get(nPar)) {
                    case "ANNOTATION":
                        n = nPar + 1;
                        do {
                        } while (!parameters.get(++nPar).equals("ANNOTATION"));
                        optionalsParameters.put(ANNOTATION, new Annotation(parameters.subList(n, nPar - 3)));
                        n = nPar + 1;
                        break;
                    case "BYTE_ORDER":
                        optionalsParameters.put(BYTE_ORDER, parameters.get(nPar + 1));
                        nPar += 1;
                        break;
                    case "DEPOSIT":
                        optionalsParameters.put(DEPOSIT, parameters.get(nPar + 1));
                        nPar += 1;
                        break;
                    case "DISPLAY_IDENTIFIER":
                        optionalsParameters.put(DISPLAY_IDENTIFIER, parameters.get(nPar + 1));
                        nPar += 1;
                        break;
                    case "FORMAT":
                        optionalsParameters.put(FORMAT, parameters.get(nPar + 1));
                        nPar += 1;
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

        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }

    }

    @Override
    protected void formatValues() {

        if (values != null) {
            final DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(getNbDecimal());

            int nbValues = values.getDimX();

            for (int i = 0; i < nbValues; i++) {
                try {
                    double doubleValue = Double.parseDouble(values.getValue(0, i));
                    values.setValue(0, i, df.format(doubleValue));
                } catch (Exception e) {
                    // Nothing
                }
            }
        }
    }

	@Override
	public Double[] getResolution() {
		
		if(ConversionType.TAB_VERB.compareTo(this.compuMethod.getConversionType())!=0)
    	{
			return new Double[]{this.compuMethod.compute(1)};
    	}
		
		return new Double[]{Double.NaN};
	}

}
