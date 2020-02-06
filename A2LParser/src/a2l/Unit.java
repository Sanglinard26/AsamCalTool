package a2l;

import static constante.SecondaryKeywords.REF_UNIT;
import static constante.SecondaryKeywords.SI_EXPONENTS;
import static constante.SecondaryKeywords.UNIT_CONVERSION;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import constante.SecondaryKeywords;
import constante.UnitType;

public final class Unit implements A2lObject, Comparable<Unit> {

    private String name;
    private char[] longIdentifier;
    private char[] display;
    private UnitType unitType;
    private Map<SecondaryKeywords, Object> optionalsParameters;

    public Unit(List<String> parameters, int beginLine, int endLine) {

        initOptionalsParameters();

        build(parameters, beginLine, endLine);
    }

    private final void initOptionalsParameters() {
        optionalsParameters = new EnumMap<SecondaryKeywords, Object>(SecondaryKeywords.class);
        optionalsParameters.put(REF_UNIT, null);
        optionalsParameters.put(SI_EXPONENTS, null);
        optionalsParameters.put(UNIT_CONVERSION, null);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws A2lObjectParsingException {

        final int nbParams = parameters.size();

        if (nbParams >= 4) {

            this.name = parameters.get(2);
            this.longIdentifier = parameters.get(3).toCharArray();
            this.display = parameters.get(4).toCharArray();
            this.unitType = UnitType.getUnitType(parameters.get(5));

            int n = 6;

            Set<SecondaryKeywords> keys = optionalsParameters.keySet();
            SecondaryKeywords keyWord;
            for (int nPar = n; nPar < nbParams; nPar++) {
                keyWord = SecondaryKeywords.getSecondaryKeyWords(parameters.get(nPar));
                if (keys.contains(keyWord)) {
                    switch (keyWord) {
                    case REF_UNIT:
                        optionalsParameters.put(REF_UNIT, parameters.get(nPar + 1));
                        nPar += 1;
                        break;
                    case SI_EXPONENTS:
                        optionalsParameters.put(SI_EXPONENTS, new SiExponents(parameters.subList(nPar + 1, nPar + 8)));
                        nPar += 7;
                        break;
                    case UNIT_CONVERSION:
                        optionalsParameters.put(UNIT_CONVERSION, new UnitConversion(parameters.subList(nPar + 1, nPar + 3)));
                        nPar += 2;
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
    public String toString() {
        return this.name;
    }

    private final class UnitConversion {
        @SuppressWarnings("unused")
        private final float gradient;
        @SuppressWarnings("unused")
        private final float offset;

        public UnitConversion(List<String> params) {
            this.gradient = Float.parseFloat(params.get(0));
            this.offset = Float.parseFloat(params.get(1));
        }
    }

    private final class SiExponents {
        @SuppressWarnings("unused")
        private final byte length;
        @SuppressWarnings("unused")
        private final byte mass;
        @SuppressWarnings("unused")
        private final byte time;
        @SuppressWarnings("unused")
        private final byte electricCurrent;
        @SuppressWarnings("unused")
        private final byte temperature;
        @SuppressWarnings("unused")
        private final byte amountOfSubstance;
        @SuppressWarnings("unused")
        private final byte luminousIntensity;

        public SiExponents(List<String> params) {
            this.length = Byte.parseByte(params.get(0));
            this.mass = Byte.parseByte(params.get(1));
            this.time = Byte.parseByte(params.get(2));
            this.electricCurrent = Byte.parseByte(params.get(3));
            this.temperature = Byte.parseByte(params.get(4));
            this.amountOfSubstance = Byte.parseByte(params.get(5));
            this.luminousIntensity = Byte.parseByte(params.get(6));
        }
    }

    @Override
    public int compareTo(Unit o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public String getProperties() {
        StringBuilder sb = new StringBuilder("<html><b><u>PROPERTIES :</u></b>");

        sb.append("<ul><li><b>Name: </b>" + name + "\n");
        sb.append("<li><b>Long identifier: </b>" + new String(longIdentifier) + "\n");
        sb.append("<li><b>Display: </b>" + new String(display) + "\n");
        sb.append("<li><b>Unit type: </b>" + unitType.name() + "\n");
        sb.append("</u></html>");

        return sb.toString();
    }
}
