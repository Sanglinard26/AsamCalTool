/*
 * Creation : 2 mars 2018
 */
package a2l;

import static constante.SecondaryKeywords.ANNOTATION;
import static constante.SecondaryKeywords.BYTE_ORDER;
import static constante.SecondaryKeywords.DEPOSIT;
import static constante.SecondaryKeywords.DISPLAY_IDENTIFIER;
import static constante.SecondaryKeywords.FORMAT;
import static constante.SecondaryKeywords.READ_ONLY;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import constante.ConversionType;
import constante.SecondaryKeywords;

public final class AxisPts extends AdjustableObject {

    @SuppressWarnings("unused")
    private char[] inputQuantity; // Reference to INPUT_QUANTITY
    private short maxAxisPoints;

    private List<Characteristic> characteristicsDependency;

    public AxisPts(List<String> parameters, int beginLine, int endLine) {

        optionalsParameters = new HashMap<SecondaryKeywords, Object>();

        build(parameters, beginLine, endLine);
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

    public final Object[] getZValues() {
        Object[] values = new Object[this.values.getDimX()];
        for (short i = 0; i < values.length; i++) {
            values[i] = this.values.getValue(0, i);
        }

        return values;
    }

    @Override
    public final void assignComputMethod(HashMap<Integer, CompuMethod> compuMethods) {
        this.compuMethod = compuMethods.get(this.conversionId);
        if (this.compuMethod == null) {
            this.compuMethod = CompuMethod.createNoCompuMethod();
        }
    }

    public final void assignCharacteristic(Characteristic characteristic) {
        if (characteristicsDependency == null) {
            characteristicsDependency = new ArrayList<Characteristic>();
        }
        characteristicsDependency.add(characteristic);
    }

    public final List<Characteristic> getCharacteristicsDependency() {
        return characteristicsDependency;
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
            this.longIdentifier = parameters.get(3).toCharArray();
            this.adress = Long.parseLong(parameters.get(4).substring(2), 16);
            this.inputQuantity = parameters.get(5).toCharArray();
            this.depositId = parameters.get(6).hashCode();
            this.maxDiff = Float.parseFloat(parameters.get(7));
            this.conversionId = parameters.get(8).hashCode();
            this.maxAxisPoints = Short.parseShort(parameters.get(9));
            this.lowerLimit = Float.parseFloat(parameters.get(10));
            this.upperLimit = Float.parseFloat(parameters.get(11));

            int n = 12;

            SecondaryKeywords keyWord;
            for (int nPar = n; nPar < nbParams; nPar++) {
                keyWord = SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar));
                switch (keyWord) {
                case ANNOTATION:
                    n = nPar + 1;
                    do {
                    } while (!parameters.get(++nPar).equals(ANNOTATION));
                    optionalsParameters.put(ANNOTATION, new Annotation(parameters.subList(n, nPar - 3)));
                    n = nPar + 1;
                    break;
                case BYTE_ORDER:
                    optionalsParameters.put(BYTE_ORDER, parameters.get(nPar + 1));
                    nPar += 1;
                    break;
                case DEPOSIT:
                    optionalsParameters.put(DEPOSIT, parameters.get(nPar + 1));
                    nPar += 1;
                    break;
                case DISPLAY_IDENTIFIER:
                    optionalsParameters.put(DISPLAY_IDENTIFIER, parameters.get(nPar + 1).toCharArray());
                    nPar += 1;
                    break;
                case FORMAT:
                    optionalsParameters.put(FORMAT, new Format(parameters.get(nPar + 1)));
                    nPar += 1;
                    break;
                case PHYS_UNIT:
                    break;
                case READ_ONLY:
                    optionalsParameters.put(READ_ONLY, true);
                    break;
                default:
                    break;
                }
            }

        } else {
            validParsing = false;
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }
        validParsing = true;
    }

    @Override
    protected void formatValues() {

        if (values != null) {
            final DecimalFormat df = new DecimalFormat();
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            String separator = new String(new char[] { dfs.getGroupingSeparator() });
            df.setDecimalFormatSymbols(dfs);
            df.setMaximumFractionDigits(getNbDecimal());

            int nbValues = values.getDimX();

            for (int i = 0; i < nbValues; i++) {
                try {
                    double doubleValue = Double.parseDouble(values.getValue(0, i).toString());
                    values.setValue(df.format(doubleValue).replace(separator, ""), 0, i);
                } catch (Exception e) {
                    // Nothing
                }
            }
        }
    }

    @Override
    public double[] getResolution() {

        if (ConversionType.TAB_VERB.equals(this.compuMethod.getConversionType())) {
            double val0 = this.compuMethod.compute(1);
            double val1 = this.compuMethod.compute(2);
            double resol = val1 - val0;
            return new double[] { resol };
        }

        return new double[] { Double.NaN };
    }

    @Override
    public boolean isValid() {
        return validParsing;
    }

    @Override
    public double getZResolution() {
        return Double.NaN;
    }

}
