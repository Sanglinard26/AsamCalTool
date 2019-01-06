/*
 * Creation : 3 janv. 2019
 */
package a2l;

import java.util.ArrayList;
import java.util.List;

public final class CompuMethod implements Comparable<CompuMethod> {

    /*
     * /begin COMPU_METHOD ident Name string LongIdentifier enum ConversionType string Format string Unit [-> COEFFS] [-> COEFFS_LINEAR] [->
     * COMPU_TAB_REF] [-> FORMULA] [-> REF_UNIT] [-> STATUS_STRING_REF] /end COMPU_METHOD
     */

    private String name;
    private String longIdentifier;
    private String conversionType;
    private String format;
    private String unit;

    public CompuMethod(List<String> parameters) {

        parameters.remove(0); // Remove /begin
        parameters.remove(0); // Remove CHARACTERISTIC

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
                    this.conversionType = parameters.get(n);
                    break;
                case 3:
                    this.format = parameters.get(n);
                    break;
                case 4:
                    this.unit = parameters.get(n);
                    break;

                default: // Cas de parametres optionels
                    break;
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

}
