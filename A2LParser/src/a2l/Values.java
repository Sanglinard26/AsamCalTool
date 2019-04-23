/*
 * Creation : 26 janv. 2018
 */
package a2l;

public final class Values {

    private final int dimX;
    private final int dimY;
    private final String[] values;
    private static int idx;

    public Values(int dimX, int dimY) {
        this.dimX = dimX;
        this.dimY = dimY;
        this.values = new String[dimX * dimY];
    }

    public final int getDimX() {
        return dimX;
    }

    public final int getDimY() {
        return dimY;
    }

    public final String getValue(int axeX, int axeY) {
        idx = axeY + dimX * axeX;
        return this.values[idx];
    }

    public final void setValue(int axeX, int axeY, String value) {
        idx = axeY + dimX * axeX;
        this.values[idx] = value;
    }

}
