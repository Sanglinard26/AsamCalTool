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
import utils.Interpolation;

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

        final int nbParams = parameters.size();

        if (nbParams >= 5) {

            this.name = parameters.get(2);
            this.longIdentifier = parameters.get(3);
            this.conversionType = ConversionType.getConversionType(parameters.get(4));
            this.format = parameters.get(5);
            this.unit = parameters.get(6);

            int n = 7;

            Set<SecondaryKeywords> keys = optionalsParameters.keySet();
            for (int nPar = n; nPar < nbParams; nPar++) {
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
                CompuTab compuTab = (CompuTab) compuTabRef;
                Float key = new Float(hex);
                return compuTab.getValuePairs().get(key);
            }
        case TAB_INTP:
            Object compuTabRefBis = this.optionalsParameters.get(COMPU_TAB_REF);
            if (compuTabRefBis instanceof CompuTab) {
                CompuTab compuTab = (CompuTab) compuTabRefBis;
                Float key = new Float(hex);
                Float value = compuTab.getValuePairs().get(key);
                if (value == null) {
                    int nbValuePairs = compuTab.getNumberValuePairs();
                    Float[] keys = compuTab.getValuePairs().keySet().toArray(new Float[nbValuePairs]);
                    int cnt = 0;
                    float x1 = 0, x2 = 0, y1 = 0, y2 = 0;
                    for (float entryKey : keys) {
                        if (entryKey > key) {
                            x1 = keys[cnt - 1];
                            x2 = keys[cnt];
                            y1 = compuTab.getValuePairs().get(new Float(x1));
                            y2 = compuTab.getValuePairs().get(new Float(x2));
                            break;
                        }
                        cnt++;
                    }
                    return Interpolation.interpLinear(x1, x2, y1, y2, key);
                }
                return value;
            }
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
            this.coeffs[0] = Float.parseFloat(params.get(0));
            this.coeffs[1] = Float.parseFloat(params.get(1));
            this.coeffs[2] = Float.parseFloat(params.get(2));
            this.coeffs[3] = Float.parseFloat(params.get(3));
            this.coeffs[4] = Float.parseFloat(params.get(4));
            this.coeffs[5] = Float.parseFloat(params.get(5));
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
            this.coeffs[0] = Float.parseFloat(params.get(0));
            this.coeffs[1] = Float.parseFloat(params.get(1));
        }

        public final float[] getCoeffs() {
            return coeffs;
        }
    }

}
