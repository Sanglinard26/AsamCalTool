/*
 * Creation : 3 janv. 2019
 */
package a2l;

import java.util.List;

public final class Measurement implements A2lObjectBuilder {

    private String name;
    @SuppressWarnings("unused")
    private String longIdentifier;
    @SuppressWarnings("unused")
    private String dataType;
    @SuppressWarnings("unused")
    private String conversion;
    @SuppressWarnings("unused")
    private int resolution;
    @SuppressWarnings("unused")
    private float accuracy;
    @SuppressWarnings("unused")
    private float lowerLimit;
    @SuppressWarnings("unused")
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

}
