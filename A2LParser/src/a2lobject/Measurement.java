/*
 * Creation : 3 janv. 2019
 */
package a2lobject;

import java.util.List;

public final class Measurement {

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

    public Measurement(List<String> parameters) {

        parameters.remove(0); // Remove /begin
        parameters.remove(0); // Remove MEASUREMENT

        if (parameters.size() >= 8) {
            for (int n = 0; n < parameters.size(); n++) {
                switch (n) {
                case 0:
                    this.name = parameters.get(n);
                    break;
                case 1:
                    this.longIdentifier = parameters.get(n);
                    break;
                case 2:
                    this.dataType = parameters.get(n);
                    break;
                case 3:
                    this.conversion = parameters.get(n);
                    break;
                case 4:
                    this.resolution = Integer.parseInt(parameters.get(n));
                    break;
                case 5:
                    this.accuracy = Float.parseFloat(parameters.get(n));
                    break;
                case 6:
                    this.lowerLimit = Float.parseFloat(parameters.get(n));
                    break;
                case 7:
                    this.upperLimit = Float.parseFloat(parameters.get(n));
                    break;
                default: // Cas de parametres optionels
                    n = parameters.size();
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

}
