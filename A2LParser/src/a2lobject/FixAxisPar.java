/*
 * Creation : 12 mars 2019
 */
package a2lobject;

import java.util.List;

public final class FixAxisPar {

    private final float offset;
    private final float shift;
    private final short numberapo;

    public FixAxisPar(List<String> parameters) {

        this.offset = Float.parseFloat(parameters.get(0));
        this.shift = Float.parseFloat(parameters.get(1));
        this.numberapo = (short) Integer.parseInt(parameters.get(2));

    }

    public final double compute(int numVal) {
        return offset + (numVal << (int) shift);
    }

    public final float getOffset() {
        return offset;
    }

    public final float getShift() {
        return shift;
    }

    public final short getNumberapo() {
        return numberapo;
    }
}
