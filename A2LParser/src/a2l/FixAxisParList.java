/*
 * Creation : 12 mars 2019
 */
package a2l;

import java.util.List;

public final class FixAxisParList {

    private final float[] axisPtsValue;

    public FixAxisParList(List<String> parameters) {

        axisPtsValue = new float[parameters.size()];

        for (short n = 0; n < axisPtsValue.length; n++) {
            axisPtsValue[n] = Float.parseFloat(parameters.get(n));
        }
    }

    public final short getNbValue() {
        return (short) axisPtsValue.length;
    }

    public final double compute(int numVal) {
        return axisPtsValue[numVal];
    }
}