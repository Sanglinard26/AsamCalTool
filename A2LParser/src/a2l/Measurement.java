/*
 * Creation : 3 janv. 2019
 */
package a2l;

import java.util.List;

public final class Measurement implements A2lObject, Comparable<Measurement> {

    private String name;
    private String longIdentifier;
    private String dataType;
    private String conversion;
    private int resolution;
    @SuppressWarnings("unused")
    private float accuracy;
    private float lowerLimit;
    private float upperLimit;

    @SuppressWarnings("unused")
    private CompuMethod compuMethod;

    public Measurement(List<String> parameters, int beginLine, int endLine) {

        build(parameters, beginLine, endLine);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public void build(List<String> parameters, int beginLine, int endLine) throws IllegalArgumentException {

        if (parameters.size() >= 8) {
            this.name = parameters.get(2);
            this.longIdentifier = parameters.get(3);
            this.dataType = parameters.get(4);
            this.conversion = parameters.get(5);
            this.resolution = Integer.parseInt(parameters.get(6));
            this.accuracy = Float.parseFloat(parameters.get(7));
            this.lowerLimit = Float.parseFloat(parameters.get(8));
            if (parameters.get(9).startsWith("0x")) { // Test pour A2L AW
                this.upperLimit = Integer.parseInt(parameters.get(9).substring(2), 16);
            } else {
                this.upperLimit = Float.parseFloat(parameters.get(9));
            }

        } else {
            throw new IllegalArgumentException("Nombre de parametres inferieur au nombre requis");
        }
    }

    @Override
    public String getProperties() {
        StringBuilder sb = new StringBuilder();

        sb.append("<ul><li><b>Name: </b>" + name + "\n");
        sb.append("<li><b>Long identifier: </b>" + longIdentifier + "\n");
        sb.append("<li><b>Data type: </b>" + dataType + "\n");
        sb.append("<li><b>Conversion: </b><a href=" + conversion + ">" + conversion + "</a>\n");
        sb.append("<li><b>Resolution: </b>" + "[" + resolution + "]" + "\n");
        sb.append("<li><b>Lower limit: </b>" + lowerLimit + "\n");
        sb.append("<li><b>Upper limit: </b>" + upperLimit + "\n");
        sb.append("</ul>");

        sb.append("<b><u>OPTIONALS PARAMETERS :\n</u></b>");

        sb.append("</html>");

        return sb.toString();
    }

    @Override
    public int compareTo(Measurement measurement) {
        return this.name.compareToIgnoreCase(measurement.name);
    }

}
