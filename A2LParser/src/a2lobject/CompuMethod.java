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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.ConversionType;
import constante.SecondaryKeywords;

public final class CompuMethod implements Comparable<CompuMethod> {

    /*
     * /begin COMPU_METHOD ident Name string LongIdentifier enum ConversionType string Format string Unit [-> COEFFS] [-> COEFFS_LINEAR] [->
     * COMPU_TAB_REF] [-> FORMULA] [-> REF_UNIT] [-> STATUS_STRING_REF] /end COMPU_METHOD
     */

    private String name;
    private String longIdentifier;
    private ConversionType conversionType;
    private String format;
    private String unit;

    private final Map<SecondaryKeywords, Object> optionalsParameters = new HashMap<SecondaryKeywords, Object>() {
        {
            put(COEFFS, null);
            put(COEFFS_LINEAR, null);
            put(COMPU_TAB_REF, null); // ToDo
            put(FORMULA, null); // ToDo
            put(REF_UNIT, null); // ToDo
            put(STATUS_STRING_REF, null); // ToDo
        }
    };

    public CompuMethod(List<String> parameters) {

        parameters.remove("/begin"); // Remove /begin
        parameters.remove("COMPU_METHOD"); // Remove CHARACTERISTIC

        if (parameters.size() == 1 || parameters.size() >= 5) {
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
                    this.conversionType = ConversionType.getConversionType(parameters.get(n));
                    break;
                case 3:
                    this.format = parameters.get(n);
                    break;
                case 4:
                    this.unit = parameters.get(n);
                    break;

                default: // Cas de parametres optionels

                    Set<SecondaryKeywords> keys = optionalsParameters.keySet();
                    for (int nPar = n; nPar < parameters.size(); nPar++) {
                        if (keys.contains(SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar)))) {
                            switch (parameters.get(nPar)) {
                            case "COEFFS": // 6 coeffs
                                optionalsParameters.put(COEFFS, new Coeffs(parameters.subList(nPar + 1, nPar + 7)));
                                break;
                            case "COEFFS_LINEAR": // 2 coeffs
                                optionalsParameters.put(COEFFS_LINEAR, new CoeffsLinear(parameters.subList(nPar + 1, nPar + 3)));
                                break;
                            case "COMPU_TAB_REF":
                                optionalsParameters.put(COMPU_TAB_REF, parameters.get(nPar + 1));
                                break;
                            case "FORMULA":
                                break;

                            default:
                                break;
                            }
                        }
                    }
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

    @Override
    public String toString() {
        return this.name;
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
            if (_coeffs[1] * _coeffs[5] == 1) {
                return hex;
            }
            return Double.NaN;

        default:
            return hex;
        }

    }

    public ConversionType getConversionType() {
        return conversionType;
    }

    public static CompuMethod createEmptyCompuMethod(String name) {
        List<String> parameters = new ArrayList<String>();
        parameters.add(name);
        return new CompuMethod(parameters);
    }

    @Override
    public boolean equals(Object obj) {
        return this.name.equals(obj.toString());
    }

    public final String getInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name : " + name + "\n");
        sb.append("LongIdentifier : " + longIdentifier + "\n");
        sb.append("ConversionType : " + conversionType + "\n");
        sb.append("Format : " + format + "\n");
        sb.append("Unit : " + unit + "\n");

        return sb.toString();
    }

    @Override
    public int compareTo(CompuMethod o) {
        return this.name.compareTo(o.name);
    }

    public final class Coeffs {

        // INT = f(PHYS), f(x) = (axx + bx + c) / (dxx + ex + f)

        private float[] coeffs;

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

        private float[] coeffs;

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
