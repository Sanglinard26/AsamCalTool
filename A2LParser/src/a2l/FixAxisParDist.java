/*
 * Creation : 12 mars 2019
 */
package a2l;

import java.util.List;

public final class FixAxisParDist {

    private final float offset;
    private final float distance;
    private final short numberapo;

    public FixAxisParDist(List<String> parameters) {

        this.offset = Float.parseFloat(parameters.get(0));
        this.distance = Float.parseFloat(parameters.get(1));
        this.numberapo = (short) Integer.parseInt(parameters.get(2));

    }

    public final double compute(int numVal) {
        return offset + (numVal * distance);
    }

    public float getOffset() {
        return offset;
    }

    public float getDistance() {
        return distance;
    }

    public short getNumberapo() {
        return numberapo;
    }
}