/*
 * Creation : 20 févr. 2019
 */
package a2lobject;

import java.util.List;

public final class AxisDescr {

    private Attribute attribute;
    private String inputQuantity;
    private String conversion;
    private int maxAxisPoints;
    private float lowerLimit;
    private float upperLimit;

    private static final int nbMandatoryFields = 6;

    public AxisDescr(List<String> parameters) {

        if (parameters.size() == 1 || parameters.size() >= 6) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.attribute = Attribute.getAttribute(parameters.get(n));
                    // System.out.println(this.name);
                    break;
                case 1:
                    this.inputQuantity = parameters.get(n);
                    break;
                case 2:
                    this.conversion = parameters.get(n);
                    break;
                case 3:
                    this.maxAxisPoints = Integer.parseInt(parameters.get(n));
                    break;
                case 4:
                    this.lowerLimit = Float.parseFloat(parameters.get(n));
                    break;
                case 5:
                    this.upperLimit = Float.parseFloat(parameters.get(n));
                    break;
                default:
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }
    }

    public static int getNbMandatoryfields() {
        return nbMandatoryFields;
    }

    public final String getInfo() {
        StringBuilder sb = new StringBuilder();

        sb.append("Attribute : " + attribute + "\n");
        sb.append("InputQuantity : " + inputQuantity + "\n");
        sb.append("Conversion : " + conversion + "\n");
        sb.append("MaxAxisPoints : " + maxAxisPoints + "\n");
        sb.append("LowerLimit : " + lowerLimit + "\n");
        sb.append("UpperLimit : " + upperLimit + "\n");

        return sb.toString();
    }

    @Override
    public String toString() {
        return getInfo();
    }

    public enum Attribute {
        CURVE_AXIS, COM_AXIS, FIX_AXIS, RES_AXIS, STD_AXIS;

        public static Attribute getAttribute(String name) {
            switch (name) {
            case "CURVE_AXIS":
                return CURVE_AXIS;
            case "COM_AXIS":
                return COM_AXIS;
            case "FIX_AXIS":
                return FIX_AXIS;
            case "RES_AXIS":
                return RES_AXIS;
            case "STD_AXIS":
                return STD_AXIS;
            default:
                return null;
            }
        }
    }

}
