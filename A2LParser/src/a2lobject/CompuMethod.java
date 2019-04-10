/*
 * Creation : 3 janv. 2019
 */
package a2lobject;

import static constante.SecondaryKeywords.COEFFS;
import static constante.SecondaryKeywords.COEFFS_LINEAR;
import static constante.SecondaryKeywords.COMPU_TAB_REF;
import static constante.SecondaryKeywords.FORMULA;
import static constante.SecondaryKeywords.REF_UNIT;
import static constante.SecondaryKeywords.STATUS_STRING_REF;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.ConversionType;
import constante.SecondaryKeywords;

public final class CompuMethod implements Comparable<CompuMethod> {

    private String name;
    @SuppressWarnings("unused")
    private String longIdentifier;
    private ConversionType conversionType;
    private String format;
    private String unit;

    private Map<SecondaryKeywords, Object> optionalsParameters;

    public CompuMethod(List<String> parameters) {

        initOptionalsParameters();

        if (parameters.size() == 1 || parameters.size() >= 5) {

            this.name = parameters.get(2);
            this.longIdentifier = parameters.get(3);
            this.conversionType = ConversionType.getConversionType(parameters.get(4));
            this.format = parameters.get(5) + "f";
            this.unit = parameters.get(6);

            int n = 7;

            Set<SecondaryKeywords> keys = optionalsParameters.keySet();
            for (int nPar = n; nPar < parameters.size(); nPar++) {
                if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                    switch (parameters.get(nPar)) {
                    case "COEFFS": // 6 coeffs
                        optionalsParameters.put(COEFFS, new Coeffs(parameters.subList(nPar + 1, nPar + 7)));
                        nPar += 6;
                        break;
                    case "COEFFS_LINEAR": // 2 coeffs
                        optionalsParameters.put(COEFFS_LINEAR, new CoeffsLinear(parameters.subList(nPar + 1, nPar + 3)));
                        nPar += 2;
                        break;
                    case "COMPU_TAB_REF":
                        optionalsParameters.put(COMPU_TAB_REF, parameters.get(nPar + 1));
                        nPar += 1;
                        break;
                    case "FORMULA":
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

    private final void initOptionalsParameters() {
        optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
        optionalsParameters.put(COEFFS, null);
        optionalsParameters.put(COEFFS_LINEAR, null);
        optionalsParameters.put(COMPU_TAB_REF, null); // ToDo
        optionalsParameters.put(FORMULA, null); // ToDo
        optionalsParameters.put(REF_UNIT, null); // ToDo
        optionalsParameters.put(STATUS_STRING_REF, null); // ToDo
    }

    @Override
    public String toString() {
        return this.name;
    }

    public final void assignConversionTable(HashMap<String, ConversionTable> conversionTables) {

        String compuTabRef = (String) this.optionalsParameters.get(COMPU_TAB_REF);
        this.optionalsParameters.put(COMPU_TAB_REF, conversionTables.get(compuTabRef));
    }

    public final double compute(double hex) {

        float[] _coeffs;

        switch (this.conversionType) {
        case IDENTICAL:
            return hex;
        case FORM:
            return hex;
        case LINEAR:
            CoeffsLinear coeffsLinear = (CoeffsLinear) this.optionalsParameters.get(COEFFS_LINEAR);
            _coeffs = coeffsLinear.getCoeffs();
            return (_coeffs[0] * hex) - _coeffs[1];
        case RAT_FUNC:
            Coeffs coeffs = (Coeffs) this.optionalsParameters.get(COEFFS);
            _coeffs = coeffs.getCoeffs();
            if (_coeffs[0] + _coeffs[2] + _coeffs[3] + _coeffs[4] == 0) {
                return (hex * _coeffs[5]) / _coeffs[1];
            }
            if (_coeffs[0] + _coeffs[3] + _coeffs[4] == 0) {
                return (hex * _coeffs[5] - _coeffs[2]) / _coeffs[1];
            }
            if (_coeffs[0] + _coeffs[1] + _coeffs[3] + _coeffs[5] == 0) {
                return _coeffs[2] / (hex * _coeffs[4]);
            }
            return Double.NaN;
        case TAB_NOINTP:
            Object compuTabRef = this.optionalsParameters.get(COMPU_TAB_REF);
            if (compuTabRef instanceof CompuTab) {
                @SuppressWarnings("unused")
                CompuTab compuTab = (CompuTab) compuTabRef;
                return hex;
            }
            return Double.NaN;
        case TAB_INTP:
            return hex;
        default:
            return Double.NaN;
        }
    }

    public final String computeString(double hex) {

        Object compuTabRef = this.optionalsParameters.get(COMPU_TAB_REF);

        switch (this.conversionType) {
        case TAB_VERB:
            if (compuTabRef instanceof CompuVTab) {
                CompuVTab compuVTab = (CompuVTab) compuTabRef;
                Float key = new Float(hex);
                return compuVTab.getValuePairs().get(key);
            }
            if (compuTabRef instanceof CompuVTabRange) {
                CompuVTabRange compuVTabRange = (CompuVTabRange) compuTabRef;
                return compuVTabRange.getStringValue(hex);
            }
            return "NaN";
        default:
            return "NaN";
        }
    }

    public final ConversionType getConversionType() {
        return conversionType;
    }

    public final boolean hasCompuTabRef() {
        return optionalsParameters.get(COMPU_TAB_REF) != null ? true : false;
    }

    public final boolean isVerbal() {
        return conversionType.compareTo(ConversionType.TAB_VERB) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        return this.name.equals(obj.toString());
    }

    public final String getFormat() {
        return format;
    }

    public final String getUnit() {
        return this.unit;
    }

    @Override
    public int compareTo(CompuMethod o) {
        return this.name.compareTo(o.name);
    }

    public final class Coeffs {

        // INT = f(PHYS), f(x) = (axx + bx + c) / (dxx + ex + f)

        private final float[] coeffs;

        public Coeffs(List<String> params) {
            this.coeffs = new float[6];
            for (int i = 0; i < coeffs.length; i++) {
                this.coeffs[i] = Float.parseFloat(params.get(i));
            }
        }

        public final float[] getCoeffs() {
            return coeffs;
        }
    }

    private final class CoeffsLinear {

        // PHYS = f(INT), f(x) = ax + b

        private final float[] coeffs;

        public CoeffsLinear(List<String> params) {
            this.coeffs = new float[2];
            for (int i = 0; i < coeffs.length; i++) {
                this.coeffs[i] = Float.parseFloat(params.get(i));
            }
        }

        public final float[] getCoeffs() {
            return coeffs;
        }
    }

}
